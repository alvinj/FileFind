# FileFind (ff)

This is a file-find utility written in Scala.

## Usage

Run `ff` by itself to see the usage information:

````
$ ff

Usage: ff [options]

  -d, --dir <value>        required; the directory to search
  -p, --search-pattern [searchPattern] (like 'StringBuilder' or '^void.*main.*')
                           required; regex patterns must match the full line
  -f, --filename-pattern [filenamePattern] (like '*.java')
                           required; the filenames to search
  -b, --before [before] (the number of lines BEFORE the search pattern to print, like 1 or 2)
  -a, --after [after]   (the number of lines AFTER the search pattern to print, like 1 or 2)
````

Here’s an example command:

````
ff -d /Users/al/Projects/Flutter -f "*.dart" -p ListTile -a 10
````

It means:

- Search the directory named */Users/al/Projects/Flutter*
- Search for files ending with the *.dart* extension
- Look for the string/pattern `ListTile`
- Print 10 lines after each instance `ListTile` is found

Sample output looks like this:

````
/Users/al/Projects/Flutter/PracticalFlutterBook/ch_05+06/flutter_book/lib/notes/NotesList.dart
----------------------------------------------------------------------------------------------
  67:                       child : ListTile(
  68:                         title : Text("${note.title}"),
  69:                         subtitle : Text("${note.content}"),
  70:                         // Edit existing note.
  71:                         onTap : () async {
  72:                           // Get the data from the database and send to the edit view.
  73:                           notesModel.entityBeingEdited = await NotesDBWorker.db.get(note.id);
  74:                           notesModel.setColor(notesModel.entityBeingEdited.color);
  75:                           notesModel.setStackIndex(1);
  76:                         }
  77:                       )
````


## Building the app

I initially build the app with [sbt-assembly](https://github.com/sbt/sbt-assembly), then create an executable with GraalVM. The steps are:

- Run `sbt assembly`, or run `assembly` from the SBT prompt
- That creates a JAR file named *target/scala-2.12/FileFind-assembly-0.1.jar*
- `cd` into the *Graal* directory
- Source the first file, i.e., `. 1setup_graal` (you’ll need to change that configuration for your system)
- Then run `2compile_graal.sh` to create the `ff` executable with GraalVM

After that, copy the `ff` executable to your *~/bin* directory, or somewhere similar.



## More information





        