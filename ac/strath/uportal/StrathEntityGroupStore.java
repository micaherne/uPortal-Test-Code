package uk.ac.strath.uportal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.iterators.EmptyIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.utils.UnImplNode;
import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.EntityTypes;
import org.jasig.portal.groups.ComponentGroupServiceDescriptor;
import org.jasig.portal.groups.EntityGroupImpl;
import org.jasig.portal.groups.EntityImpl;
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.IEntity;
import org.jasig.portal.groups.IEntityGroup;
import org.jasig.portal.groups.IEntityGroupStore;
import org.jasig.portal.groups.IEntityGroupStoreFactory;
import org.jasig.portal.groups.IGroupMember;
import org.jasig.portal.groups.ILockableEntityGroup;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.services.GroupService;

public class StrathEntityGroupStore implements IEntityGroupStore {
	private static final Log log = LogFactory.getLog(StrathEntityGroupStore.class);
	
	private StrathGroupsDAO dao = new StrathGroupsDAO();

	@Override
	public boolean contains(IEntityGroup group, IGroupMember member)
			throws GroupsException {
		Integer groupId = null;
		try {
			groupId = Integer.parseInt(group.getLocalKey());
			return dao.contains(groupId, member.getKey());
		} catch (Exception e) {
			log.debug(e);
			return false;
		} 
	}

	@Override
	public void delete(IEntityGroup group) throws GroupsException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IEntityGroup find(String key) throws GroupsException {
		log.debug("find: " + key);
		try {
			Integer groupId = Integer.parseInt(key);
			return dao.findGroup(groupId);
		} catch (Exception e) {
			log.debug(e);
			throw new GroupsException("Can't get entity group for " + key);
		}
	}

	@Override
	public Iterator findContainingGroups(IGroupMember gm)
			throws GroupsException {
		if(log.isDebugEnabled()) {
			log.debug("findContainingGroups: " + gm.getKey() + ", " + gm.getEntityType().getCanonicalName());
		}
		if(gm.isGroup()) {
			return EmptyIterator.INSTANCE;
		} else if(gm.isEntity()) {
			try {
				Map<String, String> containingGroups = dao.findContainingGroupsForEntity(gm.getKey());
				List<IEntityGroup> result = new ArrayList<IEntityGroup>();
				for(String key : containingGroups.keySet()) {
					EntityGroupImpl e = new EntityGroupImpl(key, IPerson.class);
					e.setName(containingGroups.get(key));
					result.add(e);
				}
				return result.iterator();
			} catch (Exception e) {
				log.warn(e);
			}
		}
		return EmptyIterator.INSTANCE;
	}

	@Override
	public Iterator findEntitiesForGroup(IEntityGroup group)
			throws GroupsException {
		log.debug("findEntitiesForGroup: " + group.getKey());
		Integer groupId = null;
		try {
			groupId = Integer.parseInt(group.getLocalKey());
			List<String> entities = dao.findMembers(groupId);
			log.debug("Got " + entities.size() + " results from findMembers");
			List<IEntity> result = new ArrayList<IEntity>();
			for(String memberKey : entities) {
				log.debug("Creating entity: " + memberKey + ", " + IPerson.class.getCanonicalName());
				IEntity entity = new EntityImpl(memberKey, IPerson.class);
				log.debug("Created entity is: " + entity.toString());
				
				result.add(entity);
			}
			return result.iterator();
		} catch (Exception e) {
			log.warn("Error parsing int from " + group.getLocalKey());
			return EmptyIterator.INSTANCE;
		}
	}

	@Override
	public ILockableEntityGroup findLockable(String key) throws GroupsException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] findMemberGroupKeys(IEntityGroup group)
			throws GroupsException {
		log.debug("findMemberGroupKeys: " + group.getKey());
		Integer groupId = null;
		try {
			groupId = Integer.parseInt(group.getLocalKey());
			Map<String, String> childGroups = dao.findGroups(groupId);
			return (String[]) childGroups.keySet().toArray(new String[childGroups.keySet().size()]);
		} catch (Exception e) {
			log.warn("Error parsing int from " + group.getLocalKey());
			return new String[0];
		}
	}

	@Override
	public Iterator findMemberGroups(IEntityGroup group) throws GroupsException {
		log.debug("findMemberGroups: " + group.getKey());
		Integer groupId = null;
		try {
			groupId = Integer.parseInt(group.getLocalKey());
			Map<String, String> childGroups = dao.findGroups(groupId);
			ArrayList<IEntityGroup> result = new ArrayList<IEntityGroup>();
			for(String grp : childGroups.keySet()) {
				log.debug("Creating entitygroupimpl for " + grp);
				EntityGroupImpl e = new EntityGroupImpl(grp, IPerson.class);
				e.setName(childGroups.get(grp));
				result.add(e);
			}
			return result.iterator();
		} catch (Exception e) {
			log.warn("Error parsing int from " + group.getLocalKey());
			return EmptyIterator.INSTANCE;
		}
	}

	@Override
	public IEntityGroup newInstance(Class entityType) throws GroupsException {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityIdentifier[] searchForGroups(String query, int method,
			Class leaftype) throws GroupsException {
		log.debug("searchForGroups: " + query);
		// TODO No-op
		return new EntityIdentifier[0];
	}

	@Override
	public void update(IEntityGroup group) throws GroupsException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void updateMembers(IEntityGroup group) throws GroupsException {
		throw new UnsupportedOperationException();
	}
	
	public static final class Factory implements IEntityGroupStoreFactory {

		@Override
		public IEntityGroupStore newGroupStore() throws GroupsException {
			return new StrathEntityGroupStore();
		}

		@Override
		public IEntityGroupStore newGroupStore(
				ComponentGroupServiceDescriptor svcDescriptor)
				throws GroupsException {
			return new StrathEntityGroupStore();
		}
		
	}

}
