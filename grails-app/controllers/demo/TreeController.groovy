package demo

class TreeController {

    def responds = ['json']

    TreeService treeService

    def ancestors(Long id) {
        Comment comment = treeService.read(id)
        [ancestors: treeService.ancestorsOfComment(comment)]
    }

    def descendants(Long id) {
        Comment comment = treeService.read(id)
        println comment
        [descendants: treeService.descendantsOfComment(comment)]
    }

    def children(Long id) {
        Comment comment = treeService.read(id)
        [children: treeService.childsOfComment(comment)]
    }

    def parents(Long id) {
        Comment comment = treeService.read(id)
        [parents: treeService.parentsOfComment(comment)]
    }
}