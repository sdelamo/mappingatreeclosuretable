package closuretable

class UrlMappings {

    static mappings = {
        delete "/$controller/$id"(action:"delete")
        get "/$controller"(action:"index")
        get "/$controller/$id"(action:"show")
        post "/$controller"(action:"save")
        put "/$controller/$id"(action:"update")
        patch "/$controller/$id"(action:"patch")

        "/tree/ancestors/$id"(controller: 'tree', action: 'ancestors')
        "/tree/descendants/$id"(controller: 'tree', action: 'descendants')
        "/tree/children/$id"(controller: 'tree', action: 'children')
        "/tree/parents/$id"(controller: 'tree', action: 'parents')

        "/"(controller: 'application', action:'index')
        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
