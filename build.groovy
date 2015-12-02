#!/usr/bin/env groovy

def compile() {
   println "compile..."
   ant.groovyc(srcdir: ".", destdir: ".", includes: "*.groovy", excludes: "build.groovy")
}

def test() {
   println "test..."
   ant.junit(printsummary: true, showoutput: true) {
      formatter(type: "plain")
      test(name: "ConfiguratorTest")
   }
}

ant = new AntBuilder()
ant.taskdef(name: "groovyc", classname: "org.codehaus.groovy.ant.Groovyc")
(args.size() == 0) ? compile() : args.each { "$it"() }

