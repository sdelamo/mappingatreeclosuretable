package closuretable

import grails.util.Environment

class BootStrap {

    CommentService commentService

    def init = { servletContext ->

        if (Environment.current == Environment.DEVELOPMENT ) {
            def c1 = commentService.saveComment('What\'s the cause of this bug?', 'Fran', null)
            def c2 = commentService.saveComment('I think it is a null pointer', 'Ollie', c1)
            def c3 = commentService.saveComment('No, I checked for that', 'Fran', c2)
            def c4 = commentService.saveComment('We ned to check for valid input', 'Kukla', c1)
            def c5 = commentService.saveComment('Yes, that\'s a bug', 'Ollie', c4)
            def c6 = commentService.saveComment('Yes, please add a check', 'Fran', c4)
            def c7 = commentService.saveComment('That fixed it', 'Kukla', c6)
        }

    }
    def destroy = {
    }
}
