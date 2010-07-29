package grafical.sighos.plugin.tree.model.handler;

public interface IDeltaListener {
	public void add(DeltaEvent event);
	public void remove(DeltaEvent event);
}
