/*

	forma de invocación de método call:

	def ejecucion = load 'script.groovy'
	ejecucion.call()

*/

def call(){
  
 pipeline {
    agent any
	
	environment {
	    STAGE = ''
	}
	
	parameters {
		choice choices: ['gradle', 'maven'], description: 'Indique la herramienta de construcción', name: 'buildTool'
	}

    stages {
        stage('Pipeline') {
            steps {
                println "Pipeline"
				script{				
					if (params.buildTool == 'gradle'){
						gradle()
					}
					else{
						maven()
					}
				}
            }
        }
    }
	post {
        failure {
            slackSend color: '#FF0000', message: "${env.USER} | ${env.JOB_NAME}-${env.BUILD_NUMBER} | ${env.buildTool} | Ejecución fallida en el stage: ${STAGE}\r\n Para ver la salida de la consola haga cilc en ${env.BUILD_URL}console"
        }
		success{
			slackSend color: '#00FF00', message: "${env.USER} | ${env.JOB_NAME} | ${env.buildTool} | Ejecución exitosa. Para obtener más detalles vaya a ${env.BUILD_URL}"
		}
    }
}

}

return this;
