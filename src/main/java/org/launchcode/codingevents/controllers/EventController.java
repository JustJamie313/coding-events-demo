package org.launchcode.codingevents.controllers;

import org.launchcode.codingevents.data.EventCategoryRepository;
import org.launchcode.codingevents.data.EventRepository;
import org.launchcode.codingevents.data.TagRepository;
import org.launchcode.codingevents.models.Event;
import org.launchcode.codingevents.models.EventCategory;
import org.launchcode.codingevents.models.Tag;
import org.launchcode.codingevents.models.dto.EventTagDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Created by Chris Bay
 */
@Controller
@RequestMapping("events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventCategoryRepository eventCategoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @GetMapping
    public String displayAllEvents(@RequestParam(required = false) Integer categoryID, Model model) {

        if(categoryID == null){
            model.addAttribute("title", "All Events");
            model.addAttribute("events", eventRepository.findAll());
        } else {
            Optional< EventCategory> result = eventCategoryRepository.findById(categoryID);
            if(result.isEmpty()){
                model.addAttribute("title","Invalid Category ID: "+ categoryID);
            } else {
                EventCategory category = result.get();
                model.addAttribute("title","All "+category.getName()+" Events");
                model.addAttribute("events",category.getEvents());
            }
        }

        return "events/index";
    }

    @GetMapping("create")
    public String displayCreateEventForm(Model model) {
        model.addAttribute("title", "Create Event");
        model.addAttribute(new Event());
        model.addAttribute("categories", eventCategoryRepository.findAll());
        return "events/create";
    }

    @PostMapping("create")
    public String processCreateEventForm(@ModelAttribute @Valid Event newEvent,
                                         Errors errors, Model model) {
        if(errors.hasErrors()) {
            model.addAttribute("title", "Create Event");
            return "events/create";
        }

        eventRepository.save(newEvent);
        return "redirect:";
    }

    @GetMapping("delete")
    public String displayDeleteEventForm(Model model) {
        model.addAttribute("title", "Delete Events");
        model.addAttribute("events", eventRepository.findAll());
        return "events/delete";
    }

    @PostMapping("delete")
    public String processDeleteEventsForm(@RequestParam(required = false) int[] eventIds) {

        if (eventIds != null) {
            for (int id : eventIds) {
                eventRepository.deleteById(id);
            }
        }

        return "redirect:";
    }
    @GetMapping("detail")
    public String displayEventDetails(@RequestParam Integer eventID, Model model){
        Optional<Event> result = eventRepository.findById(eventID);
        if(result.isEmpty()){
            model.addAttribute("title","Invalid Event ID: " + eventID);
        } else {
            Event event = result.get();
            model.addAttribute("title",event.getName()+" Details");
            model.addAttribute("event",event);
            model.addAttribute("tags",event.getTags());
        }
        return "events/detail";
    }
    @GetMapping("addTag")
    public String displayAddTagForm(@RequestParam Integer eventID, Model model){
        Optional<Event> result = eventRepository.findById(eventID);
        Event event = result.get();
        model.addAttribute("title","Add Tag to "+event.getName());
        model.addAttribute("tags",tagRepository.findAll());
        EventTagDTO eventTagDTO = new EventTagDTO();
        eventTagDTO.setEvent(event);
        model.addAttribute("eventTag",eventTagDTO);
        return "events/addTag";
    }
    @PostMapping("addTag")
    public String processAddTagForm(@ModelAttribute @Valid EventTagDTO eventTagDTO,Errors errors, Model model){
        if(!errors.hasErrors()){
            Event event = eventTagDTO.getEvent();
            Tag tag = eventTagDTO.getTag();
            if(!event.getTags().contains(tag)){
                event.addTag(tag);
                eventRepository.save(event);
            }
            return "redirect:detail?eventID="+event.getId();
        }

        return "redirect:addTag";
    }
}
