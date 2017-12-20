# HDCapstone
This is our senior project project page, where we are developing a Home Depot Mobile Device Document Audit System.
[Documentation for this project](https://docs.google.com/document/d/1PwwHuLURvqFmhsDOGjovVWoCgkp1QJUqv9Wb68yJOe8/edit?usp=sharing) 
# Requirements:
* ch.qos.logback:logback-classic:0.9.30
* com.google.cloud:google-cloud-vision:0.23.1-beta
* com.levigo.jbig2:levigo-jbig2-imageio:2.0
* com.logentries:logentries-appender:1.1.38
* commons-cli:commons-cli:1.4
* org.apache.pdfbox:pdfbox:2.0.7
# Arguments:
* -v //verbose: Toggles detailed logging
* -s //source-test: Runs the source test on the given input
* -i [C:/path/to/document/filename.PDF] //input: specify a PDF document for testing*
* -d [C:/path/to/documents/] //directory: specify a folder containing PDF documents for testing*
* -r //recursive: Toggles recursive folder searching when using directory input (checks folders inside of folders)
* -h //help: Shows help message
*Use only one input method at a time
# Example:
HDCapstone.jar -v -s -i "C:/files/document.PDF"
