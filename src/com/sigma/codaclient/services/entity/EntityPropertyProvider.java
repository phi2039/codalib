/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.entity;

import com.coda.efinance.schemas.common.TypeCtEntityType;
import com.coda.efinance.schemas.entitymaster.Entity;
import com.coda.efinance.schemas.entitymaster.EntityData;
import com.coda.efinance.schemas.entitymaster.EntityFlags;
import com.coda.efinance.schemas.entitymaster.EntityMember;
import com.sigma.codaclient.services.common.CodaPropertyProvider;
import com.sigma.codaclient.services.common.CodaGenericPropertyProvider;
import com.sigma.codaclient.services.common.CodaPropertyTypes;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author clance
 */
public class EntityPropertyProvider extends CodaGenericPropertyProvider<Entity> implements CodaPropertyProvider<Entity> {
  protected static EntityPropertyProvider instance;

  protected EntityPropertyProvider() throws Exception {
    super("entity",Entity.class);

    addProperty("code", CodaPropertyTypes.STRING);
    addProperty("name", CodaPropertyTypes.STRING);
    addProperty("short_name", CodaPropertyTypes.STRING);
    addProperty("type", CodaPropertyTypes.STRING);
    addProperty("members", CodaPropertyTypes.LIST);
  }
  
  public static EntityPropertyProvider getInstance() throws Exception {
    if (instance == null)
      instance = new EntityPropertyProvider();
    return instance;
  }
  
  @Override
  public <T> void setValue(String name, T value, Entity instance) throws Exception {
    switch(name) {
    case "code": // String
      instance.setCode((String)value);
      break;
    case "name": // String
      instance.setName((String)value);
      break;
    case "short_name": // String
      instance.setShortName((String)value);
      break;
    case "type": // EntityType or String
      instance.setType(TypeCtEntityType.fromValue(value.toString()));
      break;
    case "members": // List<EntityMemberProperties>
      EntityData members = new EntityData();
      List<EntityMemberProperties> memberList = (List<EntityMemberProperties>)value;
      if (memberList != null) {
        for (EntityMemberProperties m : memberList) {
          EntityMember member = new EntityMember();
          member.setValue(m.getName());
          member.setDescription(m.getDescription());
          member.setEnabled(m.isEnabled());
          EntityFlags flags = new EntityFlags();
          flags.setPayment(m.isPayment());
          members.getMember().add(member);
        }
      }
      instance.setMembers(members);
      break;
    default:
      throw new Exception ("Unsupported or unknown property: " + name);
    }  
  }

  @Override
  public void clearValue(String name, Entity instance) throws Exception {
    switch(name) {
    case "code":
      instance.setCode(null);
      break;
    case "name":
      instance.setName(null);
      break;
    case "short_name":
      instance.setShortName(null);
      break;
    case "type":
      instance.setType(null);
      break;
    case "members": // List<EntityMemberProperties>
      instance.setMembers(null);
      break;
    default:
      throw new Exception ("Unsupported or unknown property: " + name);
    }  
  }

  @Override
  public <T> T getValue(String name, Entity instance) throws Exception {
    switch(name) {
    case "code":
      return (T)instance.getCode();
    case "name":
      return (T)instance.getName();
    case "short_name":
      return (T)instance.getShortName();
    case "type":
      return (T)instance.getType().toString();
    case "members": // List<EntityMemberProperties>
      EntityData entityData = instance.getMembers();
      List<EntityMemberProperties> members = new ArrayList<>();
      for (EntityMember entityMember : entityData.getMember())
        members.add(new EntityMemberProperties(entityMember.getValue(), entityMember.getDescription(), entityMember.isEnabled(), entityMember.getFlags().isPayment()));
      return (T)members;
    default:
      throw new Exception ("Unsupported or unknown property: " + name);
    }  
  }

  @Override
  public void setDefaults(Entity instance) throws Exception {
    
  }
  
}
