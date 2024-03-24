def call(Map params) {
    // Implementación de la función
    // Accede a los parámetros usando params.abortPipeline y params.branchName

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
                error 'QualityGate ha fallado. Abortando el pipeline.'
            } else {
                echo 'QualityGate ha sido superado. Continuando con el pipeline.'
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