rootProject.name = "MiniPlaceholders-Essentials"

// Execute bootstrap.sh
exec {
    workingDir(rootDir)
    commandLine("sh", "bootstrap.sh")
}

include("libs:Utilities-OG")
