package Generators;
import java.util.ArrayList;
import java.util.function.Predicate;

public interface Generator<T>
{
	public T fromLine(String line);
	public T get(String s);
	public T getRandom();
	public T getRandom(Predicate<T> tester);
	public ArrayList<T> getAll();
}
