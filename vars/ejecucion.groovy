/*

	forma de invocación de método call:

	def ejecucion = load 'script.groovy'
	ejecucion.call()

*/

stgsToProc = [:]
map = [:]
gradleMap = [1:[name:'build', priority:1, dependencies:null],
           2:[name:'sonar', priority:2, dependencies:'build'],
           3:[name:'run', priority:3, dependencies:'build'],
           4:[name:'testapp', priority:4, dependencies:'run'],
           5:[name:'nexus', priority:5, dependencies:'build']]

mavenMap = [1:[name:'build', priority:1, dependencies:null],
           2:[name:'sonar', priority:2, dependencies:'build'],
           3:[name:'run', priority:3, dependencies:'build'],
           4:[name:'testapp', priority:4, dependencies:'run'],
           5:[name:'nexus', priority:5, dependencies:'build']]
           6:[name:'testapp', priority:4, dependencies:'run'],
           7:[name:'nexus', priority:5, dependencies:'build']]		  

def addStage(stagesStr){
    def stagesList = stagesStr.split(',')
    for(int i = 0;i<stagesList.length;i++){
        def stage = map.find { it.value.name == stagesList[i]}
        if(stage!=null){
            if(!stgsToProc.containsKey(stage.key)){
                stgsToProc.put(stage.key,stage.value)
                if(stage.value.dependencies!=null)
                    addStage(stage.value.dependencies)
            }
            else{
                STAGE_ERR_MSG = "Stage no válida: ${stagesList[i]}"
                exit 0
            }
        }
        else{
            STAGE_ERR_MSG = "Stage no válida: ${stagesList[i]}"
            exit 0
        }
    }
}

def runGradleStages(stages){
    map = gradleMap
    addStage(stages)
    def keyS = stgsToProc.sort { a, b -> a.value.priority <=> b.value.priority }.keySet()
    keyS.each {
        def stageName = stgsToProc.get(it).name
        switch(stageName) {
            case "build":
                println "g build: ${stageName}"
                break
            case "sonar":
                println "g sonar: ${stageName}"
                break
            case "run":
                println "g run: ${stageName}"
                break
            case "testapp":
                println "g testapp: ${stageName}"
                break
            case "nexus":
                println "g nexus: ${stageName}"
                break
            default:
                STAGE_ERR_MSG = "Stage no válida: ${stagesList[i]}"
                println STAGE_ERR_MSG
                break
        }
    }
}

def runMavenStages(stages){
    map = mavenMap
    addStage(stages)
    def keyS = stgsToProc.sort { a, b -> a.value.priority <=> b.value.priority }.keySet()
    keyS.each {
        def stageName = stgsToProc.get(it).name
        switch(stageName) {
            case "build":
                println "m build: ${stageName}"
                break
            case "sonar":
                println "m sonar: ${stageName}"
                break
            case "run":
                println "m run: ${stageName}"
                break
            case "testapp":
                println "m testapp: ${stageName}"
                break
            case "nexus":
                println "m nexus: ${stageName}"
                break
            default:
                STAGE_ERR_MSG = "Stage no válida: ${stagesList[i]}"
                println STAGE_ERR_MSG
                break
        }
    }
}

def call(){
  
	pipeline {
		agent any
		
		environment {
			STAGE = ''
			STAGE_ERR_MSG = ''
		}
		
		parameters {
			choice choices: ['gradle', 'maven'], description: 'Indique la herramienta de construcción', name: 'buildTool'
			string name: 'stages', defaultValue: '', description: 'Stages a ejecutar', trim:true
		}

		stages {
			stage('Pipeline') {
				steps {
					println "Pipeline"
					script{				
						if (params.buildTool == 'gradle'){
							if(params.stages == ''){
								gradle()
							}
							else{
								map = gradleMap
								runGradleStages(params.stages)
							}
						}
						else{
							if(params.stages == ''){
								maven()
							}
							else{
								map = mavenMap
								runMavenStages(params.stages)
							}
						}
					}
				}
			}
		}
		post {
			failure {
				slackSend color: '#FF0000', message: "${env.USER} | ${env.JOB_NAME}-${env.BUILD_NUMBER} | ${env.buildTool} | Ejecución fallida en el stage: ${STAGE}\r\n${STAGE_ERR_MSG}\r\nPara ver la salida de la consola haga cilc en ${env.BUILD_URL}console"
			}
			success{
				slackSend color: '#00FF00', message: "${env.USER} | ${env.JOB_NAME} | ${env.buildTool} | Ejecución exitosa. Para obtener más detalles vaya a ${env.BUILD_URL}"
			}
		}
	}
}

return this;
