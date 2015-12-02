class ConfiguratorTest extends GroovyTestCase {
   def configurator

   void setUp() {
      configurator = new Configurator()
   }

   void testParseFile() {
      def configFile = new File("temp.config")
      configFile.write("x = 1")
      def config = configurator.parseFile(configFile)
      assert config.x == 1 : "x != 1"
   }

   void testSetProperty() {
      def config = configurator.parse("x=1")
      assert config.x == 1 : "x != 1"
   }

   void testGetProperty() {
      def config = configurator.parse("x=1; y=x")
      assert config.y == 1 : "y != 1"
   }

   void testSubstitution() {
      def config = configurator.parse('x=1; y=${x}')
      assert config.y == 1 : "y != 1"
   }

   void testGrouping() {
      def configurator = new Configurator()
      def config = configurator.parse('''
         a {
            b {
               c=1
            }
         }

         y=a.b.c
      ''')
      assert config.y == 1 : "y != 1"
   }

   void testScoping() {
      def config = configurator.parse('''
         a {
            b=1

            c {
               d=b
               e=a.b
            }
         }

         y=a.c.e
      ''')
      assert config.y == 1 : "y != 1"
   }

   void testMethodCall() {
      configurator.parse('println 1')
   }

   void testMissingProperty() {
      try {
         configurator.parse('a=b')
         assert false : "Exception not thrown."
      } catch(e) {
      }
   }

   void testMissingMethod() {
      try {
         configurator.parse('a()')
         assert false : "Exception not thrown."
      } catch(e) {
      }
   }

   void testFlatten() {
      def config = configurator.parse('x=1; a { b=1 }')
      def props = new java.util.Properties()
      configurator.flatten("", configurator.binding.root, props)
      assert props.getProperty("x") == '1' : "props.x != '1'"
      assert props.getProperty("a.b") == '1' : "props.a.b != '1'"
   }

   void testToProperties() {
      configurator.parse('x=1; a { b=1 }')
      def props = configurator.toProperties()
      assert props.getProperty("x") == '1' : "props.x != '1'"
      assert props.getProperty("a.b") == '1' : "props.a.b != '1'"
   }
}

