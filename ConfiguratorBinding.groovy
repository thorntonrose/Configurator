class ConfiguratorBinding extends Binding {
   def root = [:]
   def stack = [ ["", root] ]

   void setVariable(String name, Object value) {
      def namespace = stack.last()[1]
      namespace."$name" = value
   }

   Object getVariable(String name) {
      for (int i in (stack.size() - 1)..0) {
         def namespace = stack[i][1]
         if (namespace.containsKey(name)) return namespace."$name"
      }

      throw new MissingPropertyException(name)
   }

   def push(name) {
      def scope = stack.last()[1]
      def namespace
      
      if (scope.containsKey(name)) {
         namespace = scope."$name"
      } else {
         namespace = [:]
         scope."$name" = namespace
      }

      stack.push([name, namespace])
   }
  
   def pop() {
      return stack.pop()
   }
}

