
//import scala.swing._
//import scala.swing.TabbedPane._


//object swingApp extends SimpleSwingApplication {
//  def top = new MainFrame {
//
//    title = "My Swing App"
//    contents = new TabbedPane {
//      pages += new Page("Tab 1", new BoxPanel(Orientation.Vertical) {
//        contents += new Label("This is Tab 1")
//        contents += new Button("Click me!")
//        contents += new ComboBox(List("Option 1", "Option 2", "Option 3"))
//        contents += new CheckBox("Check me!")
//      })
//      pages += new Page("Tab 2", new BoxPanel(Orientation.Vertical) {
//        contents += new Label("This is Tab 2")
//        contents += new RadioButton("Option 1")
//        contents += new RadioButton("Option 2")
//        contents += new RadioButton("Option 3")
//      })
//      pages += new Page("Tab 3", new BoxPanel(Orientation.Vertical) {
//        contents += new Label("This is Tab 3")
//        contents += new TextField(20)
//        contents += new PasswordField(20)
//        contents += new TextArea(5, 20)
//      })
//      pages += new Page("Tab 4", new BoxPanel(Orientation.Vertical) {
//        contents += new Label("This is Tab 4")
//        contents += new Slider()
//        contents += new ProgressBar()
//        contents += new ScrollPane(new Label("Scrollable content"))
//      })
//    }
//    size = new Dimension(400, 300)
//  }
//}
