package closuretable

import grails.compiler.GrailsCompileStatic
import grails.gorm.DetachedCriteria
import org.codehaus.groovy.util.HashCodeHelper
import groovy.transform.ToString

@ToString(cache=true, includeNames=true, includePackage=false)
@GrailsCompileStatic
class CommentTreePath implements Serializable {
    CommentGormEntity ancestor
    CommentGormEntity descendant
    int length

    static mapping = {
        version false
        table 'treepath'
        descendant column: 'descendant'
        ancestor column: 'ancestor'
        id composite: ['ancestor', 'descendant']
    }

    @Override
    boolean equals(other) {
        if (other instanceof CommentTreePath) {
            other.ancestorId == ancestor?.id && other.descendantId == descendant?.id
        }
    }

    @Override
    int hashCode() {
        int hashCode = HashCodeHelper.initHash()
        if (ancestor) {
            hashCode = HashCodeHelper.updateHash(hashCode, ancestor.id)
        }
        if (descendant) {
            hashCode = HashCodeHelper.updateHash(hashCode, descendant.id)
        }
        hashCode
    }

    static constraints = {
        length nullable: false
        descendant validator: { CommentGormEntity descendant, CommentTreePath treePath ->
            if (treePath.ancestor?.id) {
                if (exists(treePath.ancestor.id, descendant.id)) {
                    return ['treePath.exists']
                }
            }
        }
    }

    static boolean exists(long ancestorId, long descendantId) {
        criteriaFor(ancestorId, descendantId).count()
    }

    private static DetachedCriteria<CommentTreePath> criteriaFor(long ancestorId, long descendantId) {
        CommentTreePath.where {
            ancestor == CommentGormEntity.load(ancestorId) &&
                    descendant == CommentGormEntity.load(descendantId)
        }
    }

}
