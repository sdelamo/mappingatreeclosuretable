package closuretable

import demo.Comment
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Specification

@Rollback
@Integration
class CommentServiceIntegrationSpec extends Specification {

    @Autowired
    CommentService commentService

    List<Map> expected = [
            [a: 1, d: 1, l: 0],
            [a: 1, d: 2, l: 1],
            [a: 1, d: 3, l: 2],
            [a: 1, d: 4, l: 1],
            [a: 1, d: 5, l: 2],
            [a: 1, d: 6, l: 2],
            [a: 1, d: 7, l: 3],
            [a: 2, d: 2, l: 0],
            [a: 2, d: 3, l: 1],
            [a: 3, d: 3, l: 0],
            [a: 4, d: 4, l: 0],
            [a: 4, d: 5, l: 1],
            [a: 4, d: 6, l: 1],
            [a: 4, d: 7, l: 2],
            [a: 5, d: 5, l: 0],
            [a: 6, d: 6, l: 0],
            [a: 6, d: 7, l: 1],
            [a: 7, d: 7, l: 0],
    ]

    Comment c1
    Comment c2
    Comment c3
    Comment c4
    Comment c5
    Comment c6
    Comment c7

    def setup() {

        CommentGormEntity.withTransaction {
            c1 = commentService.saveComment('What\'s the cause of this bug?', 'Fran', null)
            c2 = commentService.saveComment('I think it is a null pointer', 'Ollie', c1)
            c3 = commentService.saveComment('No, I checked for that', 'Fran', c2)
            c4 = commentService.saveComment('We ned to check for valid input', 'Kukla', c1)
            c5 = commentService.saveComment('Yes, that\'s a bug', 'Ollie', c4)
            c6 = commentService.saveComment('Yes, please add a check', 'Fran', c4)
            c7 = commentService.saveComment('That fixed it', 'Kukla', c6)
        }
    }

    def cleanup() {
        CommentGormEntity.withTransaction {
            CommentTreePath.executeUpdate("delete CommentTreePath")
            CommentGormEntity.executeUpdate("delete CommentGormEntity")
        }
    }

    def 'test comment and comment tree path is generated correctly'() {
        when:
        def treePaths = CommentTreePath.findAll()

        then:
        treePaths.size() == expected.size()

        when:
        def ids = treePaths.collect { [a: it.ancestor.id, d: it.descendant.id, l: it.length] }

        then:
        expected.size() == ids.size()
        for (Map m : ids) {
            assert expected.find { it.a == m.a && it.d == m.d && it.l == m.l }
        }
    }

    def 'insert a new node below #5'() {
        given:
        assert CommentTreePath.count() == expected.size()

        when:
        int commentCount = CommentGormEntity.count()
        commentService.saveComment('I agree', 'Fran', c5)

        then:
        CommentGormEntity.count() == (commentCount + 1)
    }

    @Ignore
    def 'move node'() {
        given:
        assert CommentTreePath.count() == expected.size()

        when:
        commentService.moveCommentToParent(c6, c3)

        then:
        CommentGormEntity.count() == expected.size()
    }

    def 'query inmediate child'() {
        given:
        assert CommentTreePath.count() == expected.size()

        when:
        def result = commentService.childsOfComment(c4)

        then:
        result.size() == 2
        result.find { it.id = c5.id }
        result.find { it.id = c6.id }
    }

    def 'query inmediate parent'() {
        given:
        assert CommentTreePath.count() == expected.size()

        when:
        def result = commentService.parentsOfComment(c1)

        then:
        !result

        when:
        result = commentService.parentsOfComment(c4)

        then:
        result
        result.size() == 1
        result.find { it.id = c1.id }
    }

    def 'query descendants of #4'() {
        given:
        assert CommentTreePath.count() == expected.size()

        when:
        def descendants = commentService.descendantsOfComment(c4)

        then:
        descendants
        descendants.size() == 3
        descendants.find { it.id == c6.id}
        descendants.find { it.id == c5.id}
        descendants.find { it.id == c7.id}
    }

    def 'query ancestors of #7'() {
        given:
        assert CommentTreePath.count() == expected.size()

        when:
        def ancestors = commentService.ancestorsOfComment(c1)

        then:
        !ancestors

        when:
        ancestors = commentService.ancestorsOfComment(c4)

        then:
        ancestors
        ancestors.size() == 1
        ancestors.first().id == c1.id

        when:
        ancestors = commentService.ancestorsOfComment(c2)

        then:
        ancestors
        ancestors.size() == 1
        ancestors.first().id == c1.id

        when:
        ancestors = commentService.ancestorsOfComment(c7)

        then:
        ancestors
        ancestors.find { it.id == c6.id}
        ancestors.find { it.id == c4.id}
        ancestors.find { it.id == c1.id}
        ancestors.size() == 3
    }
}
