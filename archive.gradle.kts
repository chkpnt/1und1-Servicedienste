plugins {
    base
    id("org.ajoberstar.git-publish") version "2.1.3"
}

gitPublish {
    repoUri.set("git@github.com:chkpnt/1und1-Servicedienste-Archiv.git")
    repoDir.set(file("$buildDir/gitArchive"))
    branch.set("master")
    commitMessage.set("Update 1&1 Servicedienste")
    contents {
        from("build/1und1/")
    }
}