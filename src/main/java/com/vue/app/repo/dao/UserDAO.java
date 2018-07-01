package com.vue.app.repo.dao;

import com.vue.app.repo.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/*
* Al extender de CrudRepository<Entity, Id>, la interfaz gana los métodos:
*   - save()              - count()
*   - findOne()           - delete()
*   - exists()            - deleteAll()
*   - findAll()
*
* Y si queremos podemos añadir alguno para filtrar por otro campo/s.
* */
public interface UserDAO<P> extends CrudRepository<User, Long> {
    List<User> findByUser(String user);
}