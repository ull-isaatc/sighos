package grafical.sighos.plugin.editor.model;

import java.util.ArrayList;
import java.util.List;
public class ResourceTypeModel {
public static  List<Person> persons = new ArrayList<Person>();

public List<Person> getPersons() {
return persons;
}
public ResourceTypeModel() {
// Just for testing we hard-code the persons here:
Person person = new Person("Lars", "Vogelaaa");
person.setAddress(new Address());
person.getAddress().setCountry("Germany");
persons.add(person);

person = new Person("Jim", "Knopfaaaaaa");
person.setAddress(new Address());
person.getAddress().setCountry("Germany");
persons.add(person);

person = new Person("asdf", "asdfaaaa");
person.setAddress(new Address());
person.getAddress().setCountry("aaa");
persons.add(person);

person = new Person("aaaaaa", "aaaaafaaa");
person.setAddress(new Address());
person.getAddress().setCountry("bbbbb");
persons.add(person);


}
}
