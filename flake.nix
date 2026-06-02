{
  description = "Hammro School Application - Development Environment";

  inputs.nixpkgs.url = "https://flakehub.com/f/NixOS/nixpkgs/0.1"; # unstable Nixpkgs

  outputs =
    { self, ... }@inputs:

    let
      javaVersion = 21; # Match the Java release used by the Maven build

      supportedSystems = [
        "x86_64-linux"
        "aarch64-linux"
        "x86_64-darwin"
        "aarch64-darwin"
      ];
      forEachSupportedSystem =
        f:
        inputs.nixpkgs.lib.genAttrs supportedSystems (
          system:
          f {
            pkgs = import inputs.nixpkgs {
              inherit system;
              overlays = [ inputs.self.overlays.default ];
            };
          }
        );
    in
    {
      overlays.default =
        final: prev:
        let
          jdk = prev."jdk${toString javaVersion}";
        in
        {
          inherit jdk;
          maven = prev.maven.override { jdk_headless = jdk; };
        };

      devShells = forEachSupportedSystem (
        { pkgs }:
        {
          default = pkgs.mkShell {
            name = "school-management-system-dev";

            packages = with pkgs; [
              jdk
              maven
              git
              curl
              wget
              gcc
              gnumake
              jdt-language-server
            ];

            env = {
              JAVA_HOME = "${pkgs.jdk}";
              MAVEN_OPTS = "-Xmx2048m";
              PROJECT_DIR = builtins.toString ./.;
              DATABASE_FILE = "data/hammroschool";
            };
          };
        }
      );

      # Flake apps for convenient project execution
      apps = forEachSupportedSystem (
        { pkgs }:
        {
          build = {
            type = "app";
            program = toString (pkgs.writeShellScript "build" ''
              cd ${builtins.toString ./.}
              ${pkgs.maven}/bin/mvn clean install
            '');
          };

          run = {
            type = "app";
            program = toString (pkgs.writeShellScript "run" ''
              cd ${builtins.toString ./.}
              ${pkgs.maven}/bin/mvn clean javafx:run
            '');
          };

          package = {
            type = "app";
            program = toString (pkgs.writeShellScript "package" ''
              cd ${builtins.toString ./.}
              ${pkgs.maven}/bin/mvn clean package
            '');
          };
        }
      );
    };
}
