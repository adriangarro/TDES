package DES

import java.io.File
import java.lang.Double

import javafx.application.Application
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.geometry.{Insets, Pos}
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.TabPane.TabClosingPolicy
import javafx.scene.control.{Label, Tab, TabPane, TextField, _}
import javafx.scene.layout.{BorderPane, GridPane, HBox}
import javafx.scene.{Group, Scene}
import javafx.stage.{DirectoryChooser, FileChooser, Stage}
import org.apache.commons.io.FilenameUtils

/**
  *
  * Created by E. Adrián Garro Sánchez on 22/04/18.
  * Instituto Tecnológico de Costa Rica.
  *
  */

object UI {
  def main(args: Array[String]) {
    Application.launch(classOf[UI], args: _*)
  }
}

class UI extends Application {

  var fileToEncryptPath = ""
  var cryptogramToDecryptPath = ""

  override def start(primaryStage: Stage): Unit = {

    primaryStage.setTitle("TRIPLE DES")
    val root = new Group()
    val scene = new Scene(root, 380, 330)

    val tabPane = new TabPane
    tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE)

    val mainBorderPane = new BorderPane
    mainBorderPane.setCenter(tabPane)
    mainBorderPane.prefHeightProperty.bind(scene.heightProperty)
    mainBorderPane.prefWidthProperty.bind(scene.widthProperty)

    /* ENCRYPTION TAB */

    val encryptionTab = new Tab
    encryptionTab.setText("Encriptar")

    val encryptionGrid = new GridPane
    encryptionGrid.setAlignment(Pos.CENTER)
    encryptionGrid.setHgap(10)
    encryptionGrid.setVgap(10)
    encryptionGrid.setPadding(new Insets(25, 25, 25, 25))

    val encKeysType = new Label("Tipo de llaves: ")
    encryptionGrid.add(encKeysType, 0, 0)
    val encKeysTypeColl = FXCollections.observableArrayList("Texto", "Hexadecimal")
    val encKeysTypeComboBox = new ComboBox[String](encKeysTypeColl)
    encryptionGrid.add(encKeysTypeComboBox, 1, 0)

    val encKey1Label = new Label("Llave 1:")
    encryptionGrid.add(encKey1Label, 0, 1)
    val encKey1TextField = new TextField
    encryptionGrid.add(encKey1TextField, 1, 1)

    val encKey2Label = new Label("Llave 2:")
    encryptionGrid.add(encKey2Label, 0, 2)
    val encKey2TextField = new TextField
    encryptionGrid.add(encKey2TextField, 1, 2)

    val encKey3Label = new Label("Llave 3:")
    encryptionGrid.add(encKey3Label, 0, 3)
    val encKey3TextField = new TextField
    encryptionGrid.add(encKey3TextField, 1, 3)

    val encInputType = new Label("Tipo de entrada: ")
    encryptionGrid.add(encInputType, 0, 4)
    val encInputTypeColl = FXCollections.observableArrayList("Texto", "Hexadecimal", "Archivo")
    val encInputTypeComboBox = new ComboBox[String](encInputTypeColl)
    encryptionGrid.add(encInputTypeComboBox, 1, 4)

    val textLabel = new Label("Texto: ")
    val textField = new TextField

    val loadFileLabel = new Label("Archivo: ")
    val buttonLoadFile = new Button("Cargar")
    buttonLoadFile.setOnAction((e: ActionEvent) => {
      def action(e: ActionEvent): Unit = {
        val fileChooser: FileChooser = new FileChooser()
        val file: File = fileChooser.showOpenDialog(primaryStage)
        fileToEncryptPath = file.getAbsolutePath
      }
      action(e)
    })

    encInputTypeComboBox.setOnAction((e: ActionEvent) => {
      def action(e: ActionEvent): Unit = {
        if (encInputTypeComboBox.getValue == "Texto" || encInputTypeComboBox.getValue == "Hexadecimal") {
          if (encryptionGrid.getChildren.contains(loadFileLabel)) {
            encryptionGrid.getChildren.remove(loadFileLabel)
          }
          if (encryptionGrid.getChildren.contains(buttonLoadFile)) {
            encryptionGrid.getChildren.remove(buttonLoadFile)
          }
          if (!encryptionGrid.getChildren.contains(textLabel)) {
            encryptionGrid.add(textLabel, 0, 5)
          }
          if (!encryptionGrid.getChildren.contains(textField)) {
            encryptionGrid.add(textField, 1, 5)
          }
        }
        if (encInputTypeComboBox.getValue == "Archivo") {
          if (encryptionGrid.getChildren.contains(textLabel)) {
            encryptionGrid.getChildren.remove(textLabel)
          }
          if (encryptionGrid.getChildren.contains(textField)) {
            encryptionGrid.getChildren.remove(textField)
          }
          if (!encryptionGrid.getChildren.contains(loadFileLabel)) {
            encryptionGrid.add(loadFileLabel, 0, 5)
          }
          if (!encryptionGrid.getChildren.contains(buttonLoadFile)) {
            encryptionGrid.add(buttonLoadFile, 1, 5)
          }
        }
      }
      action(e)
    })

    val encBtn = new Button("ENCRIPTAR")
    val encHBBtn = new HBox(5)
    encHBBtn.setAlignment(Pos.BOTTOM_LEFT)
    encHBBtn.getChildren.add(encBtn)
    encryptionGrid.add(encHBBtn, 0, 6)

    encBtn.setOnAction((e: ActionEvent) => {
      def action(e: ActionEvent): Unit = {
        var validFields: Boolean = true
        // Any Field Incomplete?
        if (encKeysTypeComboBox.getSelectionModel.isEmpty) {
          showAlert("Advertencia", "Debe indicar el tipo de las llaves.")
          validFields = false
        }
        if (encKey1TextField.getText.trim.isEmpty) {
          showAlert("Advertencia", "Debe indicar la llave 1.")
          validFields = false
        }
        if (encKey2TextField.getText.trim.isEmpty) {
          showAlert("Advertencia", "Debe indicar la llave 2.")
          validFields = false
        }
        if (encKey3TextField.getText.trim.isEmpty) {
          showAlert("Advertencia", "Debe indicar la llave 3.")
          validFields = false
        }
        if (encInputTypeComboBox.getSelectionModel.isEmpty) {
          showAlert("Advertencia", "Debe indicar el tipo de entrada.")
          validFields = false
        }
        if (encInputTypeComboBox.getValue == "Texto" || encInputTypeComboBox.getValue == "Hexadecimal") {
          if (textField.getText.trim.isEmpty) {
            showAlert("Advertencia", "Debe indicar un texto para encriptar.")
            validFields = false
          }
        }
        if (encInputTypeComboBox.getValue == "Archivo") {
          if (fileToEncryptPath == "") {
            showAlert("Advertencia", "Debe cargar un archivo para encriptar.")
            validFields = false
          }
        }
        // Determinate if keys are different.
        if (!encKey1TextField.getText.trim.isEmpty
          && !encKey2TextField.getText.trim.isEmpty
          && !encKey3TextField.getText.trim.isEmpty) {
          val keys = Array(encKey1TextField.getText.trim,
            encKey2TextField.getText.trim,
            encKey3TextField.getText.trim
          )
          if (keys.distinct.length != keys.length) {
            showAlert("Advertencia", "Todas las llaves deben ser diferentes.")
            validFields = false
          }
        }
        // Check if hex keys are really hex, they have correct length, and are not weak.
        if (encKeysTypeComboBox.getValue == "Hexadecimal") {

          // Key 1
          if (!Tools.isHexNumber(encKey1TextField.getText.trim)) {
            showAlert("Advertencia", "La llave 1 no es hexadecimal.")
            validFields = false
          }
          if (encKey1TextField.getText.trim.length < 16) {
            showAlert("Advertencia", "La llave 1 debe tener al menos 16 caracteres.")
            validFields = false
          }
          if (Keys.weakHexKeys contains encKey1TextField.getText.trim.toLowerCase) {
            showAlert("Advertencia", "La llave 1 es débil, sustitúyala.")
            validFields = false
          }

          // Key 2
          if (!Tools.isHexNumber(encKey2TextField.getText.trim)) {
            showAlert("Advertencia", "La llave 2 no es hexadecimal.")
            validFields = false
          }
          if (encKey2TextField.getText.trim.length < 16) {
            showAlert("Advertencia", "La llave 2 debe tener al menos 16 caracteres.")
            validFields = false
          }
          if (Keys.weakHexKeys contains encKey2TextField.getText.trim.toLowerCase) {
            showAlert("Advertencia", "La llave 2 es débil, sustitúyala.")
            validFields = false
          }

          // Key 3
          if (!Tools.isHexNumber(encKey3TextField.getText.trim)) {
            showAlert("Advertencia", "La llave 3 no es hexadecimal.")
            validFields = false
          }
          if (encKey3TextField.getText.trim.length < 16) {
            showAlert("Advertencia", "La llave 3 debe tener al menos 16 caracteres.")
            validFields = false
          }
          if (Keys.weakHexKeys contains encKey3TextField.getText.trim.toLowerCase) {
            showAlert("Advertencia", "La llave 3 es débil, sustitúyala.")
            validFields = false
          }

        }
        // Check if text keys have correct length.
        if (encKeysTypeComboBox.getValue == "Texto") {
          // Key 1
          if (encKey1TextField.getText.trim.length < 8) {
            showAlert("Advertencia", "La llave 1 debe tener al menos 8 caracteres.")
            validFields = false
          }
          // Key 2
          if (encKey2TextField.getText.trim.length < 8) {
            showAlert("Advertencia", "La llave 2 debe tener al menos 8 caracteres.")
            validFields = false
          }
          // Key 3
          if (encKey3TextField.getText.trim.length < 8) {
            showAlert("Advertencia", "La llave 3 debe tener al menos 8 caracteres.")
            validFields = false
          }
        }
        // Check if hex text input is really hex.
        if (encInputTypeComboBox.getValue == "Hexadecimal") {
          if (!textField.getText.trim.isEmpty) {
            if (!Tools.isHexNumber(textField.getText.trim)) {
              showAlert("Advertencia", "El texto a encriptar no es hexadecimal.")
              validFields = false
            }
          }
        }
        /** If all fields are OK. **/
        if (validFields) {
          // Case 1: Keys = text, Input = text.
          if (encKeysTypeComboBox.getValue == "Texto") {
            if (encInputTypeComboBox.getValue == "Texto") {
              val c = TDES.encrypt(
                encKey1TextField.getText.trim,
                encKey2TextField.getText.trim,
                encKey3TextField.getText.trim,
                textField.getText.trim
              )
              showAlert2("Criptograma", Tools.stringToHex(c))
            }
          }
          // Case 2: Keys = hex, Input = text.
          if (encKeysTypeComboBox.getValue == "Hexadecimal") {
            if (encInputTypeComboBox.getValue == "Texto") {
              val c = TDES.encrypt(
                Tools.hexToString(encKey1TextField.getText.trim),
                Tools.hexToString(encKey2TextField.getText.trim),
                Tools.hexToString(encKey3TextField.getText.trim),
                textField.getText.trim
              )
              showAlert2("Criptograma", Tools.stringToHex(c))
            }
          }
          // Case 3: Keys = text, Input = hex.
          if (encKeysTypeComboBox.getValue == "Texto") {
            if (encInputTypeComboBox.getValue == "Hexadecimal") {
              val c = TDES.encrypt(
                encKey1TextField.getText.trim,
                encKey2TextField.getText.trim,
                encKey3TextField.getText.trim,
                Tools.hexToString(textField.getText.trim)
              )
              showAlert2("Criptograma", Tools.stringToHex(c))
            }
          }
          // Case 4: Keys = hex, Input = hex.
          if (encKeysTypeComboBox.getValue == "Hexadecimal") {
            if (encInputTypeComboBox.getValue == "Hexadecimal") {
              val c = TDES.encrypt(
                Tools.hexToString(encKey1TextField.getText.trim),
                Tools.hexToString(encKey2TextField.getText.trim),
                Tools.hexToString(encKey3TextField.getText.trim),
                Tools.hexToString(textField.getText.trim)
              )
              showAlert2("Criptograma", Tools.stringToHex(c))
            }
          }
          // Case 5: Keys = text/hex, Input = file.
          if (encInputTypeComboBox.getValue == "Archivo") {
            val directoryChooser = new DirectoryChooser
            val selectedDirectory = directoryChooser.showDialog(primaryStage)
            val savePropertiesPath = selectedDirectory.getAbsolutePath
            if (savePropertiesPath != null) {
              val propertiesName = "cryp"
              val fileBase64 = Tools.encoder(fileToEncryptPath)
              var cryptogram = ""
              if (encKeysTypeComboBox.getValue == "Hexadecimal") {
                cryptogram = TDES.encrypt(
                  Tools.hexToString(encKey1TextField.getText.trim),
                  Tools.hexToString(encKey2TextField.getText.trim),
                  Tools.hexToString(encKey3TextField.getText.trim),
                  fileBase64
                )
              }
              else if (encKeysTypeComboBox.getValue == "Texto") {
                cryptogram = TDES.encrypt(
                  encKey1TextField.getText.trim,
                  encKey2TextField.getText.trim,
                  encKey3TextField.getText.trim,
                  fileBase64
                )
              }
              Tools.saveFileContentInProperties(
                savePropertiesPath + "/",
                propertiesName,
                cryptogram,
                FilenameUtils.getExtension(fileToEncryptPath)
              )
              showAlert("Aviso", "Proceso terminado.")
            } else {
              showAlert("Advertencia", "No elegió donde guardar el criptograma del archivo.")
            }
          }
          // End. Case 5: Keys = text/hex, Input = file.
        }
        /** End. If all fields are OK. **/
      }
      action(e)
    })

    encryptionTab.setContent(encryptionGrid)
    tabPane.getTabs.add(encryptionTab)

    /* DECRYPTION TAB */

    val decryptionTab = new Tab
    decryptionTab.setText("Decriptar")

    val decryptionGrid = new GridPane
    decryptionGrid.setAlignment(Pos.CENTER)
    decryptionGrid.setHgap(10)
    decryptionGrid.setVgap(10)
    decryptionGrid.setPadding(new Insets(25, 25, 25, 25))

    val decKeysType = new Label("Tipo de llaves: ")
    decryptionGrid.add(decKeysType, 0, 0)
    val decKeysTypeColl = FXCollections.observableArrayList("Texto", "Hexadecimal")
    val decKeysTypeComboBox = new ComboBox[String](decKeysTypeColl)
    decryptionGrid.add(decKeysTypeComboBox, 1, 0)

    val decKey1Label = new Label("Llave 1:")
    decryptionGrid.add(decKey1Label, 0, 1)
    val decKey1TextField = new TextField
    decryptionGrid.add(decKey1TextField, 1, 1)

    val decKey2Label = new Label("Llave 2:")
    decryptionGrid.add(decKey2Label, 0, 2)
    val decKey2TextField = new TextField
    decryptionGrid.add(decKey2TextField, 1, 2)

    val decKey3Label = new Label("Llave 3:")
    decryptionGrid.add(decKey3Label, 0, 3)
    val decKey3TextField = new TextField
    decryptionGrid.add(decKey3TextField, 1, 3)

    val decInputType = new Label("Tipo de entrada: ")
    decryptionGrid.add(decInputType, 0, 4)
    val decInputTypeColl = FXCollections.observableArrayList("Hexadecimal", "Archivo")
    val decInputTypeComboBox = new ComboBox[String](decInputTypeColl)
    decryptionGrid.add(decInputTypeComboBox, 1, 4)

    val cipherHexLabel = new Label("Hexadecimal: ")
    val cipherHexField = new TextField

    val loadCryptogramLabel = new Label("Criptograma: ")
    val buttonLoadCryptogram = new Button("Cargar")
    buttonLoadCryptogram.setOnAction((e: ActionEvent) => {
      def action(e: ActionEvent): Unit = {
        val fileChooser: FileChooser = new FileChooser()
        val extFilter = new FileChooser.ExtensionFilter(
          "Properties files (*.properties)", "*.properties"
        )
        fileChooser.getExtensionFilters.add(extFilter)
        val file: File = fileChooser.showOpenDialog(primaryStage)
        cryptogramToDecryptPath = file.getAbsolutePath
      }
      action(e)
    })

    decInputTypeComboBox.setOnAction((e: ActionEvent) => {
      def action(e: ActionEvent): Unit = {
        if (decInputTypeComboBox.getValue == "Hexadecimal") {
          if (decryptionGrid.getChildren.contains(loadCryptogramLabel)) {
            decryptionGrid.getChildren.remove(loadCryptogramLabel)
          }
          if (decryptionGrid.getChildren.contains(buttonLoadCryptogram)) {
            decryptionGrid.getChildren.remove(buttonLoadCryptogram)
          }
          if (!decryptionGrid.getChildren.contains(cipherHexLabel)) {
            decryptionGrid.add(cipherHexLabel, 0, 5)
          }
          if (!decryptionGrid.getChildren.contains(cipherHexField)) {
            decryptionGrid.add(cipherHexField, 1, 5)
          }
        }
        if (decInputTypeComboBox.getValue == "Archivo") {
          if (decryptionGrid.getChildren.contains(cipherHexLabel)) {
            decryptionGrid.getChildren.remove(cipherHexLabel)
          }
          if (decryptionGrid.getChildren.contains(cipherHexField)) {
            decryptionGrid.getChildren.remove(cipherHexField)
          }
          if (!decryptionGrid.getChildren.contains(loadCryptogramLabel)) {
            decryptionGrid.add(loadCryptogramLabel, 0, 5)
          }
          if (!decryptionGrid.getChildren.contains(buttonLoadCryptogram)) {
            decryptionGrid.add(buttonLoadCryptogram, 1, 5)
          }
        }
      }

      action(e)
    })

    val decBtn = new Button("DECRIPTAR")
    val decHBBtn = new HBox(5)
    decHBBtn.setAlignment(Pos.BOTTOM_LEFT)
    decHBBtn.getChildren.add(decBtn)
    decryptionGrid.add(decHBBtn, 0, 6)
    decBtn.setOnAction((e: ActionEvent) => {
      def action(e: ActionEvent): Unit = {
        var validFields: Boolean = true
        // Any Field Incomplete?
        if (decKeysTypeComboBox.getSelectionModel.isEmpty) {
          showAlert("Advertencia", "Debe indicar el tipo de las llaves.")
          validFields = false
        }
        if (decKey1TextField.getText.trim.isEmpty) {
          showAlert("Advertencia", "Debe indicar la llave 1.")
          validFields = false
        }
        if (decKey2TextField.getText.trim.isEmpty) {
          showAlert("Advertencia", "Debe indicar la llave 2.")
          validFields = false
        }
        if (decKey3TextField.getText.trim.isEmpty) {
          showAlert("Advertencia", "Debe indicar la llave 3.")
          validFields = false
        }
        if (decInputTypeComboBox.getSelectionModel.isEmpty) {
          showAlert("Advertencia", "Debe indicar el tipo de entrada.")
          validFields = false
        }
        if (decInputTypeComboBox.getValue == "Hexadecimal") {
          if (cipherHexField.getText.trim.isEmpty) {
            showAlert("Advertencia", "Debe indicar un texto para decriptar.")
            validFields = false
          }
        }
        if (decInputTypeComboBox.getValue == "Archivo") {
          if (cryptogramToDecryptPath == "") {
            showAlert("Advertencia", "Debe cargar un archivo para decriptar.")
            validFields = false
          }
        }
        // Determinate if keys are different.
        if (!decKey1TextField.getText.trim.isEmpty
          && !decKey2TextField.getText.trim.isEmpty
          && !decKey3TextField.getText.trim.isEmpty) {
          val keys = Array(decKey1TextField.getText.trim,
            decKey2TextField.getText.trim,
            decKey3TextField.getText.trim
          )
          if (keys.distinct.length != keys.length) {
            showAlert("Advertencia", "Todas las llaves deben ser diferentes.")
            validFields = false
          }
        }
        // Check if hex keys are really hex, they have correct length, and are not weak.
        if (decKeysTypeComboBox.getValue == "Hexadecimal") {

          // Key 1
          if (!Tools.isHexNumber(decKey1TextField.getText.trim)) {
            showAlert("Advertencia", "La llave 1 no es hexadecimal.")
            validFields = false
          }
          if (decKey1TextField.getText.trim.length < 16) {
            showAlert("Advertencia", "La llave 1 debe tener al menos 16 caracteres.")
            validFields = false
          }
          if (Keys.weakHexKeys contains decKey1TextField.getText.trim.toLowerCase) {
            showAlert("Advertencia", "La llave 1 es débil, sustitúyala.")
            validFields = false
          }

          // Key 2
          if (!Tools.isHexNumber(decKey2TextField.getText.trim)) {
            showAlert("Advertencia", "La llave 2 no es hexadecimal.")
            validFields = false
          }
          if (decKey2TextField.getText.trim.length < 16) {
            showAlert("Advertencia", "La llave 2 debe tener al menos 16 caracteres.")
            validFields = false
          }
          if (Keys.weakHexKeys contains decKey2TextField.getText.trim.toLowerCase) {
            showAlert("Advertencia", "La llave 2 es débil, sustitúyala.")
            validFields = false
          }

          // Key 3
          if (!Tools.isHexNumber(decKey3TextField.getText.trim)) {
            showAlert("Advertencia", "La llave 3 no es hexadecimal.")
            validFields = false
          }
          if (decKey3TextField.getText.trim.length < 16) {
            showAlert("Advertencia", "La llave 3 debe tener al menos 16 caracteres.")
            validFields = false
          }
          if (Keys.weakHexKeys contains decKey3TextField.getText.trim.toLowerCase) {
            showAlert("Advertencia", "La llave 3 es débil, sustitúyala.")
            validFields = false
          }

        }
        // Check if text keys have correct length, and are not weak.
        if (decKeysTypeComboBox.getValue == "Texto") {
          // Key 1
          if (decKey1TextField.getText.trim.length < 8) {
            showAlert("Advertencia", "La llave 1 debe tener al menos 8 caracteres.")
            validFields = false
          }
          // Key 2
          if (decKey2TextField.getText.trim.length < 8) {
            showAlert("Advertencia", "La llave 2 debe tener al menos 8 caracteres.")
            validFields = false
          }
          // Key 3
          if (decKey3TextField.getText.trim.length < 8) {
            showAlert("Advertencia", "La llave 3 debe tener al menos 8 caracteres.")
            validFields = false
          }
        }
        // Check if hex text input is really hex.
        if (decInputTypeComboBox.getValue == "Hexadecimal") {
          if (!cipherHexField.getText.trim.isEmpty) {
            if (!Tools.isHexNumber(cipherHexField.getText.trim)) {
              showAlert("Advertencia", "El texto a decriptar no es hexadecimal.")
              validFields = false
            }
          }
        }
        /** If all fields are OK. **/
        if (validFields) {
          // Case 1: Keys = text, Input = hex.
          if (decKeysTypeComboBox.getValue == "Texto") {
            if (decInputTypeComboBox.getValue == "Hexadecimal") {
              var m = Tools.removePadding(TDES.decrypt(
                decKey1TextField.getText.trim,
                decKey2TextField.getText.trim,
                decKey3TextField.getText.trim,
                Tools.hexToString(cipherHexField.getText.trim)
              ))
              if (m == "") {
                m = "La hilera hexadecimal no representa un mensaje encriptado con TDES."
              }
              showAlert2("Texto en claro", m)
            }
          }
          // Case 2: Keys = hex, Input = hex.
          if (decKeysTypeComboBox.getValue == "Hexadecimal") {
            if (decInputTypeComboBox.getValue == "Hexadecimal") {
              val m = Tools.removePadding(TDES.decrypt(
                Tools.hexToString(decKey1TextField.getText.trim),
                Tools.hexToString(decKey2TextField.getText.trim),
                Tools.hexToString(decKey3TextField.getText.trim),
                Tools.hexToString(cipherHexField.getText.trim)
              ))
              showAlert2("Texto en claro", m)
            }
          }
          // Case 3: Keys = text/hex, Input = file.
          if (decInputTypeComboBox.getValue == "Archivo") {
            val cryptogram = Tools.loadProperties(cryptogramToDecryptPath)
            var plainText = ""
            if (decKeysTypeComboBox.getValue == "Hexadecimal") {
              plainText = Tools.removePadding(TDES.decrypt(
                Tools.hexToString(decKey1TextField.getText.trim),
                Tools.hexToString(decKey2TextField.getText.trim),
                Tools.hexToString(decKey3TextField.getText.trim),
                cryptogram.getProperty("content")
              ))
            } else if (decKeysTypeComboBox.getValue == "Texto") {
              plainText = Tools.removePadding(TDES.decrypt(
                decKey1TextField.getText.trim,
                decKey2TextField.getText.trim,
                decKey3TextField.getText.trim,
                cryptogram.getProperty("content")
              ))
            }
            val directoryChooser = new DirectoryChooser
            val selectedDirectory = directoryChooser.showDialog(primaryStage)
            var saveFilePath = selectedDirectory.getAbsolutePath
            if (saveFilePath != null) {
              saveFilePath = saveFilePath + "/decryp" + Tools.timestamp.getTime + "." + cryptogram.getProperty("extension")
              Tools.decoder(plainText, saveFilePath)
              showAlert("Aviso", "Proceso terminado.")
            } else {
              showAlert("Advertencia", "No elegió donde guardar el archivo decriptado.")
            }
          }
        }
        /** End. If all fields are OK. **/
      }
      action(e)
    })

    decryptionTab.setContent(decryptionGrid)
    tabPane.getTabs.add(decryptionTab)

    /* ABOUT TAB */

    val aboutTab = new Tab
    aboutTab.setText("Acerca de")
    val aboutHBox = new HBox
    aboutHBox.getChildren.add(new Label(
      "Instituto Tecnológico de Costa Rica\n"
        + "Criptografía\n"
        + "Algoritmo Triple DES\n"
        + "Prof. Jorge Vargas Calvo\n"
        + "Estudiantes: Adrián Garro, Josué Jiménez, Alejandro Soto\n"
        + "I Semestre, 2018")
    )
    aboutHBox.setAlignment(Pos.CENTER)
    aboutTab.setContent(aboutHBox)
    tabPane.getTabs.add(aboutTab)

    root.getChildren.add(mainBorderPane)
    primaryStage.setScene(scene)
    primaryStage.show()

  }

  /*
   * Show Alert Without Header Text (copyable).
   */
  private def showAlert2(title: String, message: String): Unit = {
    val textArea = new TextArea(message)
    textArea.setEditable(false)
    textArea.setWrapText(true)
    val gridPane = new GridPane
    gridPane.setMaxWidth(Double.MAX_VALUE)
    gridPane.add(textArea, 0, 0)
    val alert = new Alert(AlertType.INFORMATION)
    alert.setTitle(title)
    alert.getDialogPane.setContent(gridPane)
    alert.showAndWait
  }

  /*
   * Show Alert Without Header Text.
   */
  private def showAlert(title: String, message: String): Unit = {
    val alert = new Alert(AlertType.INFORMATION)
    alert.setTitle(title)
    // Header Text: null
    alert.setHeaderText(null)
    alert.setContentText(message)
    alert.showAndWait
  }

}
