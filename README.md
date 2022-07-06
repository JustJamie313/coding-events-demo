# Coding Events Exercises
  ## Purpose:
    This repository was created by LaunchCode for educational purposes. The files contained within this repository include both files that were forked from a
    LaunchCode repository and files that were subsequently edited or created by me.  The exercises and various branches of this repository step the student through
    setting up a persistent java based website, utilizing mySQL and Thymeleaf, to display information about hypothetical coding events.

  ## Current Version
    Users can display a list of all events, create new events, delete events, display all event categories, create new event categories, display details of a
    specific event, create tags to add to events, add tags to events, view tags added to specific event (within event details).

  ### Models
  - EventTagDTO
    - event
    - tag
  - AbstractEntity
    - id
  - Event (extends AbstractEntity)
    - name
    - eventDetails
      - OneToOne event/eventDetails
    - eventCategories
      - ManyToOne event/eventCategory
    - tags
      - ManyToMany event/tags
  - EventCategory (extends AbstractEntity)
    - name
    - events
      - OneToMany eventCategory/events
  - EventDetails (extends AbstractEntity)
    - description
    - contactEmail
  - Tag (extends AbstractEntity)
    - name
    - events
      - ManyToMany tags/events

  ### Data Repositories
    - EventCategoryRepository (extends CrudRepository)
    - EventRepository (extends CrudRepository)
    - TagRepository (extends CrudRepository)

  ###   Controllers
    - EventCategoryController
      - RequestMapping("eventCategories")
        - GetMapping: return "eventCategories/index"
        - GetMapping("create"): return "eventCategories/create"
        - PostMapping("create"): return "redirect:"
    - EventController
      - RequestMapping("events")
        - GetMapping: return "events/index"
        - GetMapping("create"): return "events/create"
        - PostMapping("create"): return "redirect:"
        - GetMapping("delete"): return "events/delete"
        - PostMapping("delete"): return "redirect:"
        - GetMapping("detail"): return "events/detail"
        - GetMapping("addTag"): return "events/addTag"
        - PostMapping("addTag"): return "redirect:detail?eventID="+event.getId();
    - HomeController
      - GetMapping: return "index"
    - TagController
      - RequestMapping("tags")
        - GetMapping: return "tags/index"
        - GetMapping("create"): return "tags/create"
        - PostMapping("create"): return "redirect:"
        
  ### Views
    - eventCategories(folder)
      - create
      - index
    - events(folder)
      - addTag
      - create
      - delete
      - detail
      - index
    - tags(folder)
      - create
      - index
    - fragments
      - head
      - header
    - index

## Future Updates
  ### Person
    Add classes and views:
    - Person (extend AbstractEntity)
      - name
      - category
        - ManyToOne person/category
      - details
        - OneToOne person/details
    - PersonEventDTO
      - person
      - event
    - PersonCategory
      - name
      - persons
        - OneToMany category/person
    - PersonDetails (extend AbstractEntity)
      - username
      - password
      - events
      - category
    - PersonRepository (extend CrudRepository)
    - PersonCategoryRepository (extend CrudRepository)
    - PersonController
      - RequestMapping("person")
        - GetMapping: return "person/index"
        - GetMapping("create"): return "person/create"
        - PostMapping("create"): return "redirect:"
        - GetMapping("detail"): return "person/detail"
        - PostMapping("detail"): return "redirect:"
        - GetMapping("delete"): return "person/delete"
        - PostMapping("delete"): return "redirect:"
    - Person Views
      - create
      - delete
      - detail
      - index
    
    Update the existing classes and views:
      - Add persons to eventDetails
      - Add PersonRepository to eventController
        - Add person/persons to models for create, delete, and detail mappings
      - Update events/detail view to use person/persons from model
      
    
    
