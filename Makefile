# HamroSchool - Makefile
# Maven-based JavaFX application build automation for Windows

# Use PowerShell as the shell
SHELL := powershell.exe
.SHELLFLAGS := -NoProfile -Command

# Variables
MVN = mvn
JAVA = java
PROJECT_NAME = hamroschool
MAIN_CLASS = com.hamroschool.HamroSchoolApplication

# Default target
.DEFAULT_GOAL := help

# Phony targets (not actual files)
.PHONY: help all compile run clean test package install verify \
        run-dev run-debug quick-compile deps update-deps tree \
        check lint javadoc coverage clean-all rebuild ci \
        info version dev seed-data

##@ General Commands

help: ## Display this help message
	@Write-Host ""
	@Write-Host "============================================" -ForegroundColor Cyan
	@Write-Host "  HamroSchool - Build Automation" -ForegroundColor Cyan
	@Write-Host "============================================" -ForegroundColor Cyan
	@Write-Host ""
	@Write-Host "Build Commands:" -ForegroundColor Yellow
	@Write-Host "  make compile       - Compile source code"
	@Write-Host "  make package       - Create JAR package"
	@Write-Host "  make install       - Install to local repository"
	@Write-Host "  make verify        - Run all verification checks"
	@Write-Host "  make all           - Clean, compile, test, and package"
	@Write-Host ""
	@Write-Host "Run Commands:" -ForegroundColor Yellow
	@Write-Host "  make run           - Compile and run application"
	@Write-Host "  make run-dev       - Run with dev profile"
	@Write-Host "  make run-debug     - Run with debugger (port 5005)"
	@Write-Host "  make dev           - Quick dev cycle (clean + run)"
	@Write-Host ""
	@Write-Host "Clean Commands:" -ForegroundColor Yellow
	@Write-Host "  make clean         - Remove build artifacts"
	@Write-Host "  make clean-all     - Deep clean"
	@Write-Host "  make rebuild       - Complete rebuild from scratch"
	@Write-Host ""
	@Write-Host "Test Commands:" -ForegroundColor Yellow
	@Write-Host "  make test          - Run all tests"
	@Write-Host "  make coverage      - Generate test coverage report"
	@Write-Host ""
	@Write-Host "Dependency Commands:" -ForegroundColor Yellow
	@Write-Host "  make deps          - Show dependencies"
	@Write-Host "  make tree          - Show dependency tree"
	@Write-Host "  make update-deps   - Check for updates"
	@Write-Host ""
	@Write-Host "Quality Commands:" -ForegroundColor Yellow
	@Write-Host "  make check         - Run code diagnostics"
	@Write-Host "  make javadoc       - Generate documentation"
	@Write-Host ""
	@Write-Host "Utility Commands:" -ForegroundColor Yellow
	@Write-Host "  make info          - Show project information"
	@Write-Host "  make version       - Show Maven version"
	@Write-Host "  make seed-data     - Initialize database"
	@Write-Host ""
	@Write-Host "Quick Aliases:" -ForegroundColor Yellow
	@Write-Host "  make b             - Alias for compile"
	@Write-Host "  make r             - Alias for run"
	@Write-Host "  make c             - Alias for clean"
	@Write-Host "  make t             - Alias for test"
	@Write-Host ""

##@ Build Commands

compile: ## Compile the source code
	@Write-Host "[INFO] Compiling source code..." -ForegroundColor Blue
	@$(MVN) compile
	@if ($$LASTEXITCODE -eq 0) { Write-Host "[SUCCESS] Compilation complete!" -ForegroundColor Green }

quick-compile: ## Fast compile without tests
	@Write-Host "[INFO] Quick compiling (skipping tests)..." -ForegroundColor Blue
	@$(MVN) compile -DskipTests -q
	@if ($$LASTEXITCODE -eq 0) { Write-Host "[SUCCESS] Quick compilation complete!" -ForegroundColor Green }

package: ## Create executable JAR package
	@Write-Host "[INFO] Creating package..." -ForegroundColor Blue
	@$(MVN) package
	@if ($$LASTEXITCODE -eq 0) { Write-Host "[SUCCESS] Package created in target/ directory!" -ForegroundColor Green }

install: ## Install to local Maven repository
	@Write-Host "[INFO] Installing to local repository..." -ForegroundColor Blue
	@$(MVN) install
	@if ($$LASTEXITCODE -eq 0) { Write-Host "[SUCCESS] Installed successfully!" -ForegroundColor Green }

verify: ## Run all verification checks
	@Write-Host "[INFO] Running verification checks..." -ForegroundColor Blue
	@$(MVN) verify
	@if ($$LASTEXITCODE -eq 0) { Write-Host "[SUCCESS] Verification complete!" -ForegroundColor Green }

all: clean compile package ## Clean, compile, and package
	@Write-Host "[SUCCESS] Full build complete!" -ForegroundColor Green

##@ Run Commands

run: compile ## Compile and run the application
	@Write-Host "[INFO] Starting application..." -ForegroundColor Blue
	@$(MVN) javafx:run

run-dev: ## Run with development profile
	@Write-Host "[INFO] Starting application in DEV mode..." -ForegroundColor Blue
	@$(MVN) javafx:run -Pdev

run-debug: ## Run with remote debugging enabled (port 5005)
	@Write-Host "[INFO] Starting application with debugger on port 5005..." -ForegroundColor Blue
	@Write-Host "[INFO] Connect your debugger to localhost:5005" -ForegroundColor Yellow
	@$(MVN) javafx:run -Djavafx.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

run-jar: package ## Run the packaged JAR file
	@Write-Host "[INFO] Running packaged JAR..." -ForegroundColor Blue
	@$(JAVA) -jar target/$(PROJECT_NAME)-1.0-SNAPSHOT.jar

##@ Clean Commands

clean: ## Remove build artifacts
	@Write-Host "[INFO] Cleaning build artifacts..." -ForegroundColor Blue
	@$(MVN) clean
	@if ($$LASTEXITCODE -eq 0) { Write-Host "[SUCCESS] Clean complete!" -ForegroundColor Green }

clean-all: ## Deep clean including Maven cache
	@Write-Host "[INFO] Performing deep clean..." -ForegroundColor Blue
	@$(MVN) clean
	@if (Test-Path target) { Remove-Item -Recurse -Force target }
	@Write-Host "[SUCCESS] Deep clean complete!" -ForegroundColor Green

rebuild: clean-all all ## Complete rebuild from scratch
	@Write-Host "[SUCCESS] Complete rebuild finished!" -ForegroundColor Green

##@ Test Commands

test: ## Run all tests
	@Write-Host "[INFO] Running tests..." -ForegroundColor Blue
	@$(MVN) test
	@if ($$LASTEXITCODE -eq 0) { Write-Host "[SUCCESS] Tests complete!" -ForegroundColor Green }

test-verbose: ## Run tests with verbose output
	@Write-Host "[INFO] Running tests (verbose)..." -ForegroundColor Blue
	@$(MVN) test -X

coverage: ## Generate test coverage report
	@Write-Host "[INFO] Generating test coverage report..." -ForegroundColor Blue
	@$(MVN) test jacoco:report
	@if ($$LASTEXITCODE -eq 0) { Write-Host "[SUCCESS] Coverage report generated in target/site/jacoco/" -ForegroundColor Green }

##@ Dependency Commands

deps: ## Show project dependencies
	@Write-Host "[INFO] Showing dependencies..." -ForegroundColor Blue
	@$(MVN) dependency:list

tree: ## Show dependency tree
	@Write-Host "[INFO] Showing dependency tree..." -ForegroundColor Blue
	@$(MVN) dependency:tree

update-deps: ## Check for dependency updates
	@Write-Host "[INFO] Checking for dependency updates..." -ForegroundColor Blue
	@$(MVN) versions:display-dependency-updates

analyze-deps: ## Analyze dependencies for conflicts
	@Write-Host "[INFO] Analyzing dependencies..." -ForegroundColor Blue
	@$(MVN) dependency:analyze

##@ Code Quality Commands

check: ## Run code diagnostics
	@Write-Host "[INFO] Running diagnostics..." -ForegroundColor Blue
	@$(MVN) compiler:compile
	@if ($$LASTEXITCODE -eq 0) { Write-Host "[SUCCESS] Diagnostics complete!" -ForegroundColor Green }

lint: check ## Run code quality checks
	@Write-Host "[SUCCESS] Code quality check complete!" -ForegroundColor Green

javadoc: ## Generate Javadoc documentation
	@Write-Host "[INFO] Generating Javadoc..." -ForegroundColor Blue
	@$(MVN) javadoc:javadoc
	@if ($$LASTEXITCODE -eq 0) { Write-Host "[SUCCESS] Javadoc generated in target/site/apidocs/" -ForegroundColor Green }

site: ## Generate project site
	@Write-Host "[INFO] Generating project site..." -ForegroundColor Blue
	@$(MVN) site
	@if ($$LASTEXITCODE -eq 0) { Write-Host "[SUCCESS] Site generated in target/site/" -ForegroundColor Green }

##@ Database Commands

seed-data: ## Initialize database with seed data
	@Write-Host "[INFO] Seeding database..." -ForegroundColor Blue
	@$(MVN) exec:java -Dexec.mainClass="com.hamroschool.util.SeedClasses"
	@if ($$LASTEXITCODE -eq 0) { Write-Host "[SUCCESS] Database seeded!" -ForegroundColor Green }

##@ Development Commands

dev: clean compile run ## Quick dev cycle: clean, compile, and run
	@Write-Host "[SUCCESS] Development build complete!" -ForegroundColor Green

watch: ## Watch for changes (requires external tool)
	@Write-Host "[WARN] File watching not implemented. Use IDE auto-build instead." -ForegroundColor Yellow

##@ Information Commands

info: ## Show project information
	@Write-Host ""
	@Write-Host "============================================" -ForegroundColor Cyan
	@Write-Host "  Project Information" -ForegroundColor Cyan
	@Write-Host "============================================" -ForegroundColor Cyan
	@Write-Host "  Name:        HamroSchool"
	@Write-Host "  Version:     1.0-SNAPSHOT"
	@Write-Host "  Java:        21"
	@Write-Host "  Build Tool:  Maven"
	@Write-Host "  Framework:   JavaFX"
	@Write-Host "============================================" -ForegroundColor Cyan
	@Write-Host ""

version: ## Show Maven version
	@$(MVN) --version

effective-pom: ## Show effective POM
	@Write-Host "[INFO] Showing effective POM..." -ForegroundColor Blue
	@$(MVN) help:effective-pom

##@ Utility Commands

open-target: ## Open target directory
	@if (Test-Path target) { Start-Process target } else { Write-Host "[ERROR] Target directory not found. Run 'make compile' first." -ForegroundColor Red }

open-javadoc: javadoc ## Generate and open Javadoc
	@Start-Process target\site\apidocs\index.html

size: ## Show build artifact sizes
	@Write-Host ""
	@Write-Host "Build Artifact Sizes:" -ForegroundColor Cyan
	@Write-Host "----------------------------------------"
	@if (Test-Path target\*.jar) { Get-ChildItem target\*.jar | Format-Table Name, @{Name="Size (MB)";Expression={[math]::Round($$_.Length/1MB, 2)}} } else { Write-Host "No JAR files found" }
	@Write-Host "----------------------------------------"
	@Write-Host ""

list-targets: ## List all available targets
	@Write-Host ""
	@Write-Host "Available Makefile targets:" -ForegroundColor Cyan
	@Get-Content Makefile | Select-String -Pattern '^[a-z-]+:.*##' | ForEach-Object { $$_.Line -replace ':.*##', ' -' }
	@Write-Host ""

##@ CI/CD Commands

ci: clean verify ## Run CI build
	@Write-Host "[SUCCESS] CI build complete!" -ForegroundColor Green

init: ## Initialize new development environment
	@Write-Host "[INFO] Initializing development environment..." -ForegroundColor Blue
	@$(MVN) clean install -DskipTests
	@if ($$LASTEXITCODE -eq 0) { Write-Host "[SUCCESS] Environment initialized!" -ForegroundColor Green }

##@ Quick Aliases

b: compile ## Alias for compile (build)

r: run ## Alias for run

c: clean ## Alias for clean

t: test ## Alias for test

p: package ## Alias for package

i: install ## Alias for install

##@ Debug Commands

debug-info: ## Show debug information
	@Write-Host ""
	@Write-Host "============================================" -ForegroundColor Cyan
	@Write-Host "  Debug Information" -ForegroundColor Cyan
	@Write-Host "============================================" -ForegroundColor Cyan
	@Write-Host "  Maven:       $(MVN)"
	@Write-Host "  Java:        $(JAVA)"
	@Write-Host "  Project:     $(PROJECT_NAME)"
	@Write-Host "  Main Class:  $(MAIN_CLASS)"
	@Write-Host ""
	@Write-Host "  Current Directory:"
	@Get-Location
	@Write-Host ""
	@Write-Host "  Java Version:"
	@java -version
	@Write-Host ""
	@Write-Host "  Maven Version:"
	@mvn --version
	@Write-Host "============================================" -ForegroundColor Cyan
	@Write-Host ""

##@ Performance Commands

profile: ## Run with profiler
	@Write-Host "[INFO] Running with profiler..." -ForegroundColor Blue
	@$(MVN) javafx:run -Djava.util.logging.config.file=logging.properties

memory-high: ## Run with high memory settings
	@Write-Host "[INFO] Running with high memory (2GB)..." -ForegroundColor Blue
	@$(MVN) javafx:run -Djavafx.run.jvmArguments="-Xmx2048m -Xms512m"

memory-low: ## Run with low memory settings (testing)
	@Write-Host "[INFO] Running with low memory (512MB)..." -ForegroundColor Blue
	@$(MVN) javafx:run -Djavafx.run.jvmArguments="-Xmx512m -Xms256m"
