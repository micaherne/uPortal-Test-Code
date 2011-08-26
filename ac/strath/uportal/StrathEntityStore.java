package uk.ac.strath.uportal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.groups.EntityGroupImpl;
import org.jasig.portal.groups.EntityImpl;
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.IEntity;
import org.jasig.portal.groups.IEntityStore;
import org.jasig.portal.groups.IEntityStoreFactory;
import org.jasig.portal.security.IPerson;

public class StrathEntityStore implements IEntityStore {
	private static final Log log = LogFactory.getLog(StrathEntityStore.class);

	@Override
	public IEntity newInstance(String key) throws GroupsException {
		log.debug("newInstance: " + key);
		return newInstance(key, null);
	}

	@Override
	public IEntity newInstance(String key, Class type) throws GroupsException {
		log.debug("newInstance: " + key + ", " + type.getCanonicalName());
		return new EntityImpl(key, type);
	}
	
	public static final class Factory implements IEntityStoreFactory {

		@Override
		public IEntityStore newEntityStore() throws GroupsException {
			return new StrathEntityStore();
		}
		
	}

}
