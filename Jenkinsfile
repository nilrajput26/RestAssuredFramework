// Declarative Jenkins pipeline for the Restful-Booker API tests.
// Needs: JDK + Maven tools configured in Jenkins, the Allure Jenkins plugin,
// and a "Secret text" credential with id "booker-password".
pipeline {
    agent any

    tools {
        jdk 'jdk-21'          // name of the JDK in Manage Jenkins > Tools
        maven 'maven-3'       // name of the Maven install in Manage Jenkins > Tools
    }

    parameters {
        choice(name: 'ENV',   choices: ['qa', 'dev'],                         description: 'Config file: config/<ENV>.properties')
        choice(name: 'SUITE', choices: ['testng.xml', 'testng-smoke.xml'],   description: 'TestNG suite to run')
    }

    triggers {
        cron('H 2 * * *')     // nightly regression; pushes are triggered by the SCM webhook
    }

    environment {
        BOOKER_PASSWORD = credentials('booker-password')   // injected as an env var, masked in logs
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run API tests') {
            steps {
                bat "mvn -B clean test -Denv=${params.ENV} -DsuiteXmlFile=${params.SUITE}"
                // On a Linux agent use: sh "mvn -B clean test ..."
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
            allure results: [[path: 'target/allure-results']]
            archiveArtifacts artifacts: 'target/logs/*.log', allowEmptyArchive: true
        }
    }
}
