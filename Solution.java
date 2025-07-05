package lab9;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.*;
import java.sql.Array;
import java.util.*;

public class Solution {
    public static void main(String[] args) throws Exception {

        part1();
        part2_12();
        part2_34();
    }

    private static void part1() throws Exception {
        Cat kitty = new Cat();
        kitty.name = "Kitty";

        Scanner scanner = new Scanner(System.in);
        System.out.println("enter a method name:");
        String methodName = scanner.next();

        Class<?> clazz = kitty.getClass();
        Method method = findMethod(methodName, clazz);
        if (method == null) {
            System.out.println("method not found");
            return;
        }
        Parameter[] parameters = method.getParameters();
        if (parameters.length > 0) {
            System.out.println("enter the parameter:");
            String parameter = scanner.next();
            method.invoke(kitty, parameter);
        }
        else {
            method.invoke(kitty);
        }
    }

    private static Method findMethod(String methodName, Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (int index = 0; index < methods.length; ++index) {
            if (methods[index].getName().equals(methodName))
                return methods[index];
        }
        return null;
    }

    private static void part2_12() throws Exception {
        Cat kitty = new Cat();
        kitty.name = "Kitty";
        kitty.age = 3;
        kitty.numMiceEaten = 20;
        kitty.weight = 3.5f;
        kitty.favouriteBed = "blanket";

        Person owner = new Person();
        owner.name = "Harry Potter";
        owner.age = 14;
        owner.ownedPet = kitty;

        printObject(kitty, "");
        printObject(owner, "");
    }

    private static void part2_34() throws Exception {
        Cat kitty = new Cat();
        kitty.name = "Kitty";
        kitty.age = 3;
        kitty.numMiceEaten = 20;
        kitty.weight = 3.5f;
        kitty.favouriteBed = "blanket";

        Person owner = new Person();
        owner.name = "Harry Potter";
        owner.age = 14;
        owner.ownedPet = kitty;
        kitty.owner = owner;

        writeObject(kitty, "kitty.txt");

        Cat loadedKitty = (Cat)readObject("kitty.txt");

        if (
            loadedKitty.name.equals(kitty.name) &&
            loadedKitty.age == kitty.age &&
            loadedKitty.numMiceEaten == kitty.numMiceEaten &&
            loadedKitty.weight == kitty.weight &&
            loadedKitty.favouriteBed.equals(kitty.favouriteBed) &&
            loadedKitty.owner.name.equals(kitty.owner.name) &&
            loadedKitty.owner.age == kitty.owner.age
        ) {
            System.out.println("kitty loaded succesfully!");
        } else {
            System.out.println("faulty kitty =(");
        }
    }

    private static void printObject(Object obj, String prefix) throws IllegalAccessException {
        Class<?> clazz = obj.getClass();
        System.out.println(prefix + clazz.getName());
        Field[] fields = getAllFields(obj);
        for (int index = 0; index < fields.length; ++index) {
            Field field = fields[index];
            Object value = field.get(obj);

            // This commented line is for 2.1
            // System.out.println(prefix + field.getName() + " (" + field.getType().getSimpleName() + ") = " + value.toString());

            // ...and this is for 2.2
            System.out.print(prefix + field.getName() + " (" + field.getType().getSimpleName() + ")");
            if (!field.getType().isPrimitive() && field.getType() != String.class && value != null) {
                System.out.println(":");
                printObject(value, prefix + ">");
            } else if (value == null) {
                System.out.println(" = null");
            } else {
                System.out.println(" = " + value.toString());
            }
        }
    }

    private static Field[] getAllFields(Object obj) {
        // Since we want public and private fields, we must use getDeclaredFields
        // But, that does not return fields for the base class -- hence we loop up the class hierarchy
        Class<?> clazz = obj.getClass();
        ArrayList<Field> allFields = new ArrayList<>();
        while (clazz != Object.class) {  // i.e. stop when there are no more base classes
            Field[] fields = clazz.getDeclaredFields();
            allFields.addAll(Arrays.asList(fields));
            clazz = clazz.getSuperclass();
        }
        return allFields.toArray(new Field[0]);
    }

    private static void writeObject(Object obj, String filename) throws FileNotFoundException, IllegalAccessException {
        // To avoid looping forever (e.g. when cat references owner, and owner references cat), we keep track of
        // which objects we've already written, and which we're about to write
        // We start from the passed object, write it and its primitives, then write any objects it referenced, then any
        // objects they referenced that are not already written, etc.
        ArrayList<Object> objectsToWrite = new ArrayList<>();
        HashSet<Object> objectsWritten = new HashSet<>();
        objectsToWrite.add(obj);
        try (PrintWriter writer = new PrintWriter(filename)) {
            while (objectsToWrite.size() > 0) {
                Object nextObject = objectsToWrite.remove(objectsToWrite.size() - 1);
                writeSubObject(nextObject, objectsToWrite, objectsWritten, writer);
                objectsWritten.add(nextObject);
            }
        }
    }

    private static void writeSubObject(Object obj, ArrayList<Object> objectsToWrite, HashSet<Object> objectsWritten, PrintWriter writer) throws IllegalAccessException {
        // This writes a single object to a PrintWriter
        Class<?> clazz = obj.getClass();
        Field[] fields = getAllFields(obj);

        // First write a line saying the object's id (hash), class name, and number of fields
        writer.println(obj.hashCode() + "," + clazz.getName() + "," + fields.length);

        // Then write two lines per field, saying the name, type, and value. For objects, the
        // value is their hash (id)
        for (int index = 0; index < fields.length; ++index) {
            Field field = fields[index];
            Object value = field.get(obj);
            writer.println(field.getName());
            if (!field.getType().isPrimitive() && field.getType() != String.class && value != null) {
                writer.println("object " + value.hashCode());
                if (value != obj && !objectsToWrite.contains(value) && !objectsWritten.contains(value))
                    objectsToWrite.add(value);  // make a note that we need to write this object later
            } else if (value == null) {
                writer.println("null");
            } else {
                writer.println("value " + value.toString());
            }
        }
    }

    private static class ObjectReference {
        // Used in readObject below, to temporarily track object references that need setting
        ObjectReference(long containingObjectId, Field field, long targetObjectId) {
            this.containingObjectId = containingObjectId;
            this.field = field;
            this.targetObjectId = targetObjectId;
        }
        long containingObjectId;
        Field field;
        long targetObjectId;
    }

    private static Object readObject(String filename) throws Exception {
        HashMap<Long, Object> idToObject = new HashMap<>();
        ArrayList<ObjectReference> objectReferences = new ArrayList<>();  // this will store object references that we 'fix up' at the end to point to  he right things
        Object rootObject = null;
        try (
            FileReader reader = new FileReader(filename);
            Scanner scanner = new Scanner(reader)
        ) {
            while (scanner.hasNext()) {
                // Read the 'header' line giving the class name etc.
                String classLine = scanner.nextLine();
                String[] bits = classLine.split(",");
                assert bits.length == 3;
                long id = Long.parseLong(bits[0]);
                String className = bits[1];
                int numFields = Integer.parseInt(bits[2]);

                // Create the corresponding object, using the default constructor
                Class<?> clazz = Class.forName(className);
                Object obj = clazz.getDeclaredConstructor().newInstance();
                Field[] fields = getAllFields(obj);
                if (rootObject == null) {
                    rootObject = obj;  // we'll return this, the first object
                }

                // Read each field's name and parse its value
                for (int fieldIndex = 0; fieldIndex < numFields; ++fieldIndex) {
                    String fieldName = scanner.nextLine();
                    Field field = findField(fields, fieldName);
                    String valueLine = scanner.nextLine();
                    if (valueLine.startsWith("object ")) {
                        long valueId = Long.parseLong(valueLine.split(" ")[1]);
                        objectReferences.add(new ObjectReference(id, field, valueId));
                    } else if (valueLine.startsWith("value ")) {
                        String value = valueLine.substring(6);  // i.e. chop off "value "
                        Class<?> fieldType = field.getType();
                        if (fieldType == String.class) {
                            field.set(obj, value);
                        } else if (fieldType == int.class) {
                            field.set(obj, Integer.parseInt(value));
                        } else if (fieldType == float.class) {
                            field.set(obj, Float.parseFloat(value));
                        } else {
                            throw new RuntimeException("unsupported type");
                        }
                    } else if (valueLine.equals("null")) {
                        field.set(obj, null);
                    } else {
                        throw new RuntimeException("unexpected value");
                    }
                }

                // Store the loaded object, associated with its id so other objects can refer to it
                idToObject.put(id, obj);
            }
        }

        // Fix all the object references to be correct. We can't do this during loading, since
        // an object may reference one that we've not yet read from the file
        for (int index = 0; index < objectReferences.size(); ++index) {
            ObjectReference ref = objectReferences.get(index);
            Object containingObject = idToObject.get(ref.containingObjectId);
            Object targetObject = idToObject.get(ref.targetObjectId);
            ref.field.set(containingObject, targetObject);
        }

        return rootObject;
    }

    private static Field findField(Field[] fields, String name) {
        // Given an array of fields from getDeclaredFields or whatever, return the one with the specified name
        for (int i = 0; i < fields.length; ++i) {
            if (fields[i].getName().equals(name))
                return fields[i];
        }
        return null;
    }
}
