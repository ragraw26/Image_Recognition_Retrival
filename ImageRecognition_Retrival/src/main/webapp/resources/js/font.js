
var textID = document.getElementById("text"); // go and take the Text from the ID
var text = textID.innerHTML; // Take the text from the
var toChange = text.split(""); // Separrate each letter into array
var newText = ""; // buffer text
var aClassName = ["red", "green", "blue"]; // class name that you want
var colorNumber = 0; // counter to loop into your class

for (var i=0, ii=toChange.length; i<ii; i++){
		if(colorNumber == aClassName.length){ // if you reach the end of your class array
		colorNumber = 0; //Set it back to 0
	}
	// Add between each letter the span with your class
	newText += "<span class="+aClassName[colorNumber]+">"+toChange[i]+"<\/span>";
	colorNumber++
}
// Output your text into the web
textID.innerHTML = newText;
