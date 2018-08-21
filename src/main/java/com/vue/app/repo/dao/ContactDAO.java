package com.vue.app.repo.dao;

import com.vue.app.repo.model.Contact;
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
public interface ContactDAO extends CrudRepository<Contact, String> {
    List<Contact> findByUser(String user);
}