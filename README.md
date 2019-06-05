# Demand Paging

## What is this?
This is a simulated demand pager takes in 6 command line arguments when calling the program, as outlined in the spec. This demand pager features the 3 replacement algorithms as specified in the specification.

## How to Compile

### On  Windows and NYU Compute Servers
To compile and run the program, load the file paging.java to your working directory. 
Next, using a terminal window with the javac and java commands, compile the java program by using the command "javac paging.java"
a) To run the program after compilation use the command "java paging M P S J N R". 

### FreeStyle Compiling
Open the paging.java file and copy and paste the source code into wherever you want to compile. 
The class declaration (line 25) may need modifications for freestyle compiling. 
The program must be compiled into a java unit using the Java compiler using at a minimum Java 7.
a) To run the program use the java command, call the program name, and all the arguments necessary.

### Notes about Compiling and Running
The random_numbers file MUST be in the same directory as the java file, and readable.
The random_numbers MUST be similar to that of NYU Classes, with the SAME file name (random_numbers), file extension (none), and file structure (1 number per line, reasonably infinte relative to the processes (NYU Classes was 100k numbers)).
The execution of the program MUST be with the directory with the java/class files as the working directory.
Invocation arguments MUST follow spec. ProgramName is NOT an argument. Calling Java is NOT an argument.

Quotation marks (and appostrophes) above are for differantion purposes, DO NOT use them when running commands in the terminal.
All input must still follow the specifications indicated.
M P S J N R shown above are placeholders for the actual arugments as specified in the specification.
All arguments must be compliant according to the specification.
This program does NOT support debugging, as we did NOT need to support it, according to the spec.

## Error Codes
Error Type 1.1 - You do not have enough arguments! - Please check the spec and have the 6 arguments specified.

Warning Type 1.2: You have more than 7 arguments! Only the first 6 will be considered. - Please check the spec and have only the 6 arguments specified. This warning will not appear for 7 argument invoations, as the program assumes that the last argument is the debug argument as specified in the spec. This program does NOT support debugging, as we did NOT need to support it. 

Error Type 1.3: You have a invalid integer argument! - Please check the spec and your input, and make sure your input args are in the right place and there are no typos. This check only catches the first error befor exiting. Your argument may have more than 1 error though. Absolutely no added characters or letters for integer arguments.

Error Type 2.1: Your replacement algorithm string is invalid (Not all letters)! - Please check the spec for valid replacement algorithm strings. (lifo, lru, random). Absolutely no numbers.

Error Type 2.2: Your replacement algorithm string is invalid (Not lifo, lru, or random)! - Please check the spec for valid replacement algorithm strings. (lifo, lru, random). Absolutely no added characters.

Error Type 3: Your job mix number is invalid! - Pleace check the spec for valud job mix numbers (1, 2, 3, 4).

Error Type 4: random-numbers file was not found in this directory. - Please see not above. random-numbers file must be in the SAME directory as the java and class files. Please make sure your random-numbers file is also extensionless, and readable.

Error Type 5: Memory Corruption. - There was a bit flip, that caused an error in this case. Please use another system to complete.

## Other Related Repositories:
* [Linker](https://github.com/tojimjiang/systems-linker)  
* [Scheduler](https://github.com/tojimjiang/systems-scheduler)  
* [Banker](https://github.com/tojimjiang/systems-banker)  