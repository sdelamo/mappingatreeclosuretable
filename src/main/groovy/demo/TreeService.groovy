package demo

import groovy.transform.CompileStatic

@CompileStatic
interface TreeService {
    List<Comment> descendantsOfComment(Comment ancestorComment)
    List<Comment> childsOfComment(Comment commentEntity)
    List<Comment> ancestorsOfComment(Comment commentEntity)
    List<Comment> parentsOfComment(Comment commentEntity)
    void deleteComment(Comment comment)
    void moveCommentToParent(Comment comment, Comment newAncestor)
    Comment saveComment(String comment, String author, Comment ancestorComment)
    Comment read(Long id)
}