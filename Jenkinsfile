pipeline {
    agent any
    parameters {
        string(defaultValue: '$BUILD_NUMBER', description: '', name: 'VERSION', trim: true)
        choice(name: 'DEPLOYMENT_DESTINATION', choices: ['jatbama-develop', 'jatbama-live'], description: '')
    }
    tools {
        maven 'maven4'
        jdk 'jdk11'
        groovy 'groovy4'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'git-new', url: 'http://192.168.30.30:10580/NikaPardaz/Jat.git']]])
            }
        }
        stage('build'){
            steps{           
                sh 'mvn -X clean install'
            }
        }
        stage('upload jar to nexus'){
            steps{
                nexusArtifactUploader artifacts: [[artifactId: 'samatapp-wallet', classifier: '', file: 'samatapp-wallet/target/samatapp-wallet-3.0-SNAPSHOT-exec.jar', type: 'jar']], credentialsId: 'jenkins-nexus', groupId: 'com.core', nexusUrl: '192.168.30.30:10680', nexusVersion: 'nexus3', protocol: 'http', repository: 'maven-jat-snapshots', version: '${VERSION}'
            }
        }
        stage('deploy to server'){
            steps{
                sshagent(credentials: ['jatbama-develop']) {
                  sh 'ssh -o StrictHostKeyChecking=no -l samat jatbama-develop wget -O /home/samat/jat/samatapp-wallet.jar http://192.168.30.30:10680/repository/maven-jat-snapshots/com/core/samatapp-wallet/${VERSION}/samatapp-wallet-${VERSION}.jar'
                  sh 'ssh -o StrictHostKeyChecking=no -l samat jatbama-develop sudo systemctl restart jat7275'
                }                                    
            }
        }
        stage("Code Quality Check via SonarQube") {
            steps {
                git credentialsId: 'gogs', url: 'http://192.168.30.30:10580/NikaPardaz/Jat.git'
                script {
                def scannerHome = tool 'sonarqube';
                    withSonarQubeEnv("sonarqube") {
                    sh "${tool("sonarqube")}/bin/sonar-scanner -Dsonar.projectKey=Jat-sonar -Dsonar.sources=. -Dsonar.host.url=http://192.168.30.27:9000 -Dsonar.login=a9d48099a358fac152dd50cea6a630efebc185a4 -Dsonar.java.binaries=/home/samat/work/slave_builder/workspace/Jat/samatapp-wallet/target/classes -Dsonar.sourceEncoding=UTF-8 -Dsonar.language=java"
                    }
                        
                }
            }
        }                                                                
    }
}