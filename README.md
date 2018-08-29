MobileDocumentScanner was a project for the second year Software Hut module, designed to the specification of a local company, by Team Untitled.

It was intended to scan pages of a document using an android mobile phone camera, perform optical character recognition to automatically tag descriptors, encrypt and save them to a server.
In it's current state, the login mechanism and encryption is incomplete due to time constraints with a reduced size team, and the server connectivity was provided through tunneling software for the test environment.

Team Untitled consisted of:

  Joshua Henderson (HendersonJ97): Team coordinator and lead developer
  
  Edward Glen (Drago6777): Database designer and developer
  
  Amber-Louise Diskin: Class designer and developer
  
Team Untitled also consisted of two more members who neglected to contribute.


app/src/main/java/com/untitled/mobiledocumentscanner/BitmapUtil.java - Utility file to convert from bitmap to bytes and back

app/src/main/java/com/untitled/mobiledocumentscanner/CameraActivity.java - Android activity class to take and upload photos

app/src/main/java/com/untitled/mobiledocumentscanner/DetailActivity.java - Android activity class to display and modify the details or tags of a single document

app/src/main/java/com/untitled/mobiledocumentscanner/Document.java - Serializable data structure to hold details about a single document

app/src/main/java/com/untitled/mobiledocumentscanner/GalleryActivity.java - Android activity class to display a gallery of saved documents

app/src/main/java/com/untitled/mobiledocumentscanner/GalleryAdapter.java - Android adapter class to streamline to control of the gallery

app/src/main/java/com/untitled/mobiledocumentscanner/ImageActivity.java - Android activity class to display a large view of a single page of a document, and provide controls

app/src/main/java/com/untitled/mobiledocumentscanner/ImageSurfaceView.java - Android activity class to display a camera preview

app/src/main/java/com/untitled/mobiledocumentscanner/JSONParser.java - HTTP request and parse a JSON object

app/src/main/java/com/untitled/mobiledocumentscanner/LoginActivity.java - Android activity class to provide an email login mechanism

Not currently completed, system uses example details to provide identification

app/src/main/java/com/untitled/mobiledocumentscanner/Page.java - Serializable data structure to provide information about a single document page

app/src/main/java/com/untitled/mobiledocumentscanner/ServerActivity.java - Android activity class to retrieve IP address information from the user

app/src/main/java/com/untitled/mobiledocumentscanner/TagAdapter.java - Android adapter class to manage a list of tags for one document

app/src/main/java/com/untitled/mobiledocumentscanner/ViewPagerAdapter.java - Android adapter class to show pages of the document
