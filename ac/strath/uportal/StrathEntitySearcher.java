package uk.ac.strath.uportal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.IEntitySearcher;
import org.jasig.portal.groups.IEntitySearcherFactory;

public class StrathEntitySearcher implements IEntitySearcher {
	private static final Log log = LogFactory.getLog(StrathEntitySearcher.class);

	@Override
	public EntityIdentifier[] searchForEntities(String query, int method,
			Class type) throws GroupsException {
		log.debug("searchForEntities: " + query + ", " + type.getCanonicalName());
		// TODO No-op
		return new EntityIdentifier[0];
	}
	
	public static final class Factory implements IEntitySearcherFactory {

		@Override
		public IEntitySearcher newEntitySearcher() throws GroupsException {
			return new StrathEntitySearcher();
		}
		
	}

}
