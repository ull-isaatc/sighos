package grafical.sighos.plugin.editor.model;

import java.util.ArrayList;
import java.util.List;
public class MiModelo {
public static  List<Person> persons = new ArrayList<Person>();

public List<Person> getPersons() {
return persons;
}
public MiModelo() {
// Just for testing we hard-code the persons here:
Person person = new Person("Lars", "Vogel");
person.setAddress(new Address());
person.getAddress().setCountry("Germany");
persons.add(person);

person = new Person("Jim", "Knopf");
person.setAddress(new Address());
person.getAddress().setCountry("Germany");
persons.add(person);

person = new Person("asdf", "asdf");
person.setAddress(new Address());
person.getAddress().setCountry("aaa");
persons.add(person);

person = new Person("aaaaaa", "aaaaaf");
person.setAddress(new Address());
person.getAddress().setCountry("bbbbb");
persons.add(person);


}
}
