package ru.macrobit.geoservice.common;

import org.springframework.data.annotation.Id;

import java.io.IOException;

public abstract class Entity {
    @Id
    protected String id;
    protected String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//	@Override
//	public boolean equals(Object obj) {
//		if (!obj.getClass().equals(this.getClass())) {
//			return false;
//		}
//		Entity entity = (Entity) obj;
//		return (this.id == null ? null == entity.id : this.id.equals(entity.id))
//				&& (this.name == null ? null == entity.name : this.name
//						.equals(entity.name));
//	}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        return id != null ? id.equals(entity.id) : entity.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        try {
            return GraphUtils.MAPPER.writeValueAsString(this);
        } catch (IOException e) {
            return "Entity [id=" + this.id + ", name=" + this.name + "]";
        }
    }
}
