package es.ull.isaatc.mosinet.pattern.dao;

public interface IDao<Model, ModelId> {
	
	public Model load(ModelId id);
	public void syncronize(Model elementModel);
	public void delete(Model elementModel);
	
}
