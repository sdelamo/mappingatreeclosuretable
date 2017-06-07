package closuretable

import demo.Comment
import demo.TreeService
import grails.compiler.GrailsCompileStatic
import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.util.logging.Slf4j

@Slf4j
@GrailsCompileStatic
@Transactional
class CommentService implements TreeService {

    @Transactional(readOnly = true)
    @Override
    List<Comment> descendantsOfComment(Comment comment) {
        CommentGormEntity entity = comment as CommentGormEntity
        ancestorsTreePathCriteria(entity).list()*.descendant
    }

    DetachedCriteria<CommentTreePath> ancestorsTreePathCriteria(CommentGormEntity entity) {
        CommentTreePath.where {
            ancestor == entity && descendant != entity
        }
    }

    @Transactional(readOnly = true)
    @Override
    List<Comment> ancestorsOfComment(Comment comment) {
        CommentGormEntity entity = comment as CommentGormEntity
        CommentTreePath.where {
            descendant == entity && ancestor != entity
        }.list()*.ancestor
    }

    @Transactional(readOnly = true)
    @Override
    List<Comment> childsOfComment(Comment comment) {
        CommentTreePath.where {
            length == 1 && ancestor == (comment as CommentGormEntity)
        }.list()*.descendant
    }

    @Override
    List<Comment> parentsOfComment(Comment comment) {
        CommentTreePath.where {
            length == 1 && descendant == (comment as CommentGormEntity)
        }.list()*.ancestor
    }

    @Override
    void deleteComment(Comment comment) {
        CommentGormEntity entity = comment as CommentGormEntity
        ancestorsTreePathCriteria(entity).deleteAll()
        for ( Comment descendant : descendantsOfComment(comment)) {
            deleteComment(descendant)
        }
        entity.delete()
    }

    @Override
    void moveCommentToParent(Comment comment, Comment newAncestor) {

    }

    @Override
    Comment saveComment(String comment, String author, Comment ancestorComment) {
        def c = new CommentGormEntity(comment: comment, author: author)
        if ( !c.save() ) {
            log.error "Could not save comment ${c.errors}"
        }

        def pathToSelf = new CommentTreePath(ancestor: c, descendant: c, length: 0)
        if (!pathToSelf.save()) {
            log.error "Could not save pathToSelf ${pathToSelf.errors}"
        }
        if ( ancestorComment != null) {
            def treePath = new CommentTreePath(ancestor: ancestorComment, descendant: c, length: 1)
            if ( !treePath.save() ) {
                log.error "Could not save tree path ${treePath.errors}"
            }

            List<CommentTreePath> commentTreePaths = CommentTreePath.where {
                descendant == ancestorComment && ancestor != ancestorComment
            }.list()
            if ( commentTreePaths ) {
                for(CommentGormEntity grandAncestor : commentTreePaths*.ancestor) {

                    int length = CommentTreePath.where { descendant == ancestorComment && ancestor == grandAncestor }.get()?.length ?: 0
                    treePath = new CommentTreePath(ancestor: grandAncestor, descendant: c, length: length + 1)
                    if ( treePath.validate() ) {
                        if ( !treePath.save() ) {
                            log.error "Could not save ancestor path ${treePath.errors}"
                        }
                    }
                }
            }
        }
        c
    }

    @Transactional(readOnly = true)
    @Override
    Comment read(Long id) {
        CommentGormEntity.read(id)
    }
}
