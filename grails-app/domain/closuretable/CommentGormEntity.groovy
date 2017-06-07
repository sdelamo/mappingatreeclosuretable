package closuretable

import demo.Comment
import grails.compiler.GrailsCompileStatic
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
@GrailsCompileStatic
class CommentGormEntity implements Comment {
    String comment
    String author

    static mapping = {
        table 'comment'
        id column: 'comment_id'
        comment type: 'text'
        version false
    }

    static constraints = {
        comment nullable: false
        author nullable: false
    }

    String toString() {
        "${author}: $comment"
    }
}