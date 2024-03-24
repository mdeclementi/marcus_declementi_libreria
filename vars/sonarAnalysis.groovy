def call(Map params) {
    def abortPipeline = params.abortPipeline ?: false
    def branchName = params.branchName ?: 'master'
    def scannerHome = tool 'sonar-scanner'
    
    withEnv(["PATH+SONAR=${scannerHome}/bin"]) {
        sh """
            sonar-scanner \
            -Dsonar.projectKey=sonarqube \
            -Dsonar.sources=. \
            -Dsonar.host.url=http://localhost:9000 \
            -Dsonar.token=squ_217b85f8c5815385dfa19b71a1aa03bb0079e16d
        """
    }

    script {
        timeout(time: 5, unit: 'MINUTES') {
            if (shouldAbortPipeline(abortPipeline, branchName)) {
                error 'Quality Gate no funciono.'
            } else {
                echo 'Quality Gate si funciona.'
            }
        }
    }
}

def shouldAbortPipeline(boolean abortPipeline, String branchName) {
    if (branchName == 'master') {
        return true
    }
    if (branchName.startsWith('hotfix/')) {
        return true
    }

    return false
}
