class Configurator {
   def classLoader = new GroovyClassLoader()
   def binding = new ConfiguratorBinding()
   def script

   def parseFile(configFile) {
      return parse(configFile.text)
   }

   def parse(configText) {
      script = classLoader.parseClass(configText).newInstance()
      script.metaClass.invokeMethod = { name, args -> invokeMethod(delegate, name, args) }
      script.metaClass.getProperty = { name -> binding.getVariable(name) }
      script.binding = binding
      script.run()

      return binding.root
   }

   def invokeMethod(delegate, name, args) {
     def result

     if ((args.size() == 1) && (args[0] instanceof Closure)) {
        binding.push(name)
        result = args[0].call()
        binding.pop()
     } else {
         def metaMethod = script.metaClass.getMetaMethod(name, args)

         if (metaMethod) {
            result = metaMethod.invoke(delegate, args)
         } else {
            throw new MissingMethodException(name, getClass(), args)
         }
      }

     return result
   }

   def toProperties() {
      def props = new java.util.Properties()
      flatten("", binding.root, props)
      return props
   }

   def flatten(prefix, namespace, props) {
      for (key in namespace.keySet()) {
         def value = namespace."$key"

         if (value instanceof Map) {
            flatten("${prefix}${key}.", value, props)
         } else {
            props.setProperty("${prefix}${key}", "$value")
         }
      }
   }

   static void main(String[] args) {
      def configurator = new Configurator()
      def config = configurator.parseFile(new File(args[0]))

      println "map: $config"
      println "properties: ${configurator.toProperties()}"
   }
}

