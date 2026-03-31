package com.dao;

import com.model.Article;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ArticleDaoHibernate implements ArticleDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session openSession() {
        return sessionFactory.openSession();
    }

    @Override
    public List<Article> findAll() {
        Session session = openSession();
        String sql = "SELECT * FROM articles";
        List<Article> list = session.createNativeQuery(sql, Article.class).list();
        session.close();
        return list;
    }

    @Override
    public Article findById(Long id) {
        Session session = openSession();
        String sql = "SELECT * FROM articles WHERE id = :id";
        Article article = session.createNativeQuery(sql, Article.class)
                                 .setParameter("id", id)
                                 .uniqueResult();
        session.close();
        return article;
    }

    @Override
    public List<Article> findByCategory(String category) {
        Session session = openSession();
        String sql = "SELECT * FROM articles WHERE category = :cat";
        List<Article> list = session.createNativeQuery(sql, Article.class)
                                    .setParameter("cat", category)
                                    .list();
        session.close();
        return list;
    }

    @Override
    public Article save(Article article) {
        Session session = openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(article);
            
            tx.commit();
            return article;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    @Override
    public void deleteById(Long id) {
        Session session = openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            
            String sql = "DELETE FROM articles WHERE id = :id";
            session.createNativeQuery(sql)
                   .setParameter("id", id)
                   .executeUpdate(); 
            
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public long count() {
        Session session = openSession();
        String sql = "SELECT COUNT(*) FROM articles";
        Number count = (Number) session.createNativeQuery(sql).uniqueResult();
        session.close();
        return count != null ? count.longValue() : 0;
    }
}