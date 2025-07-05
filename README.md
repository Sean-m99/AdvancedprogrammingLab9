
Dynamic Method Calls
Write a program that instantiates a new pets.Cat. 
It should then prompt the user to enter an action the cat should perform (corresponding to a method name on cat). 
Call the method entered by the user, using reflec- tion, and passing no parameters.
Check that your approach works for methods that take zero parameters, and that you get an exception if the user enters a method that expects a parameter.
Modify the code to check (using reflection) whether the chosen method takes a parameter, and if so, to ask the user to enter that parameter, before making the method call with it.


Reflective I/O
Write a function that takes an object as input, and prints its class name, then prints the names, types and values of each of its public and private fields (included those inherited from base classes), using reflection to find which fields are present on the provided instance. For values of object (reference) fields, print the result of toString().
Adapt your function to print object (reference) fields recursively, i.e. call- ing itself to print the object they refer to. Ensure that the output repre- sents the object structure, i.e. which fields belong to the top-level object as opposed to its object fields. A natural way to do this would be by indenting the ‘child’ fields to reflect the structure, as in Assessed Exercise 1.

3. Adapt your code to write to a file instead of printing. Write another function that can read this file, and recreate the original object hierarchy, using reflection to instiate the classes and call their default constructors, then to set the values of the fields.

5. Adapt your approach to handle the case that there are circular references among objects (e.g. by maintaining a HashSet of objects already written, and including each only once).
Congratulations, you have just re-implemented the basic functionality of the Java serialization mechanism!
