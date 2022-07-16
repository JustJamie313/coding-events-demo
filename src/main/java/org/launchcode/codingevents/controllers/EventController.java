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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

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
    public String displayEvents(@RequestParam(required = false) Integer categoryId, @RequestParam(required = false) Integer tagId, @RequestParam(required = false) List<Integer> tagIds, Model model) {
        if(isNull(categoryId) && isNull(tagId) && isNull(tagIds)){
            model.addAttribute("title", "All Events");
            model.addAttribute("events", eventRepository.findAll());
            model.addAttribute("tags",tagRepository.findAll());
        } else if(isNull(categoryId) && !isNull(tagId)){
            Optional<Tag> result = tagRepository.findById(tagId);
            if(result.isEmpty()){
                model.addAttribute("title","Invalid Tag ID: " + tagId);
            } else {
                Tag tag = result.get();
                model.addAttribute("title","Events with tag: " + tag.getDisplayName());
                model.addAttribute("events",tag.getEvents());
                model.addAttribute("tags",tag);
            }
        } else if(isNull(categoryId) && !isNull(tagIds)){
            List<Tag> tags = new ArrayList<>();
            for(Integer eachTagId:tagIds){
                tags.add(tagRepository.findById(eachTagId).get());
            }
            if(tags.isEmpty()){
                model.addAttribute("title","Invalid Tag IDs: " + tagIds.toString());
            } else {
                List<Event> events = new ArrayList<>();
                List<String> tagNames = new ArrayList<>();
                List<Tag> allTags = new ArrayList<>();
                for(Tag eachTag:tags){
                    if(!tagNames.contains(eachTag.getDisplayName())){
                        tagNames.add(eachTag.getDisplayName());
                    }
                    List<Event> eventsWithTag = eachTag.getEvents();
                    for(Event eventWithTag:eventsWithTag){
                        if(!events.contains(eventWithTag)){
                            events.add(eventWithTag);
                            for(Tag t:eventWithTag.getTags()){
                                if(!allTags.contains(t)){
                                    allTags.add(t);
                                }
                            }
                        }
                    }
                }
                model.addAttribute("title","Events containing one or more of the tags: "+tagNames.toString());
                model.addAttribute("events",events);
                model.addAttribute("tags",allTags);
            }

        } else if(!isNull(categoryId) && isNull(tagId) && isNull(tagIds)){
            Optional<EventCategory> result = eventCategoryRepository.findById(categoryId);
            List<Tag> tags = new ArrayList<>();
            if(result.isEmpty()){
                model.addAttribute("title","Invalid Category ID: " + categoryId);
            } else {
                EventCategory category = result.get();
                model.addAttribute("title","Events in category: " + category.getName());
                model.addAttribute("categoryId",categoryId);
                List<Event> events = category.getEvents();
                model.addAttribute("events", events);
                for(Event event:events){
                    for(Tag tag:event.getTags()){
                        if(!tags.contains(tag)){
                            tags.add(tag);
                        }
                    }
                }
                model.addAttribute("tags",tags);
            }
        } else if(!isNull(categoryId) && !isNull(tagId)){
            Optional<EventCategory> categoryResult = eventCategoryRepository.findById(categoryId);
            if(categoryResult.isEmpty()){
                model.addAttribute("title","Invalid Category ID: " + categoryId);
            } else {
                Optional<Tag> tagResult = tagRepository.findById(tagId);
                if (tagResult.isEmpty()) {
                    model.addAttribute("title", "Invalid Tag ID: " + tagId);
                } else {
                    EventCategory category = categoryResult.get();
                    Tag tag = tagResult.get();
                    List<Event> eventsInCategory = category.getEvents();
                    List<Event> eventsInCategoryWithTag = new ArrayList<>();
                    List<Tag> eventTags = new ArrayList<>();
                    for(Event event : eventsInCategory){
                        List<Tag> tags = event.getTags();
                        for(Tag eachTag:tags){
                            eventTags.add(eachTag);
                            if(eachTag.getId()==tagId){
                                eventsInCategoryWithTag.add(event);
                            }
                        }
                    }
                    model.addAttribute("title","Events in Category: " + category.getName() + " with Tag: " + tag.getDisplayName());
                    model.addAttribute("categoryId",categoryId);
                    model.addAttribute("events",eventsInCategoryWithTag);
                    model.addAttribute("tags",eventTags);
                }
            }
        } else if(!isNull(categoryId) && !isNull(tagIds)){
            Optional<EventCategory> categoryResult = eventCategoryRepository.findById(categoryId);
            if(categoryResult.isEmpty()){
                model.addAttribute("title","Invalid Category ID: " + categoryId);
            } else {
                List<Tag> tags = new ArrayList<>();
                for(Integer eachTagId:tagIds){
                    tags.add(tagRepository.findById(eachTagId).get());
                }
                if(tags.isEmpty()){
                    model.addAttribute("title","Invalid Tag IDs: " + tagIds.toString());
                } else {
                    EventCategory category = categoryResult.get();
                    List<Event> eventsInCategory = category.getEvents();
                    List<Event> eventsInCategoryWithTag = new ArrayList<>();
                    List<String> tagNames = new ArrayList<>();
                    List<Tag> allTags = new ArrayList<>();
                    for(Event event:eventsInCategory){
                        List<Tag> eventTags = event.getTags();
                        for(Tag eachEventTag:eventTags){
                            for(Tag eachSubmittedTag:tags){
                                if(!tagNames.contains(eachSubmittedTag.getDisplayName())){
                                    tagNames.add(eachSubmittedTag.getDisplayName());
                                }
                                if(eachEventTag.getId() == eachSubmittedTag.getId()){
                                    if(!eventsInCategoryWithTag.contains(event)){
                                        for(Tag t:event.getTags()){
                                            if(!allTags.contains(t)){
                                                allTags.add(t);
                                            }
                                        }
                                        eventsInCategoryWithTag.add(event);
                                    }
                                }
                            }
                        }
                    }
                    model.addAttribute("title","Events in Category: " + category.getName() + " containing one or more of the Tags: " + tagNames.toString());
                    model.addAttribute("events",eventsInCategoryWithTag);
                    model.addAttribute("categoryId",categoryId);
                    model.addAttribute("tags",allTags);
                }
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
    public String displayEventDetails(@RequestParam Integer eventId, Model model) {

        Optional<Event> result = eventRepository.findById(eventId);

        if (result.isEmpty()) {
            model.addAttribute("title", "Invalid Event ID: " + eventId);
        } else {
            Event event = result.get();
            model.addAttribute("title", event.getName() + " Details");
            model.addAttribute("event", event);
            model.addAttribute("tags", event.getTags());
        }

        return "events/detail";
    }

    // responds to /events/add-tag?eventId=13
    @GetMapping("add-tag")
    public String displayAddTagForm(@RequestParam Integer eventId, Model model){
        Optional<Event> result = eventRepository.findById(eventId);
        Event event = result.get();
        model.addAttribute("title", "Add Tag to: " + event.getName());
        model.addAttribute("tags", tagRepository.findAll());
        EventTagDTO eventTag = new EventTagDTO();
        eventTag.setEvent(event);
        model.addAttribute("eventTag", eventTag);
        return "events/add-tag.html";
    }

    @PostMapping("add-tag")
    public String processAddTagForm(@ModelAttribute @Valid EventTagDTO eventTag,
                                    Errors errors,
                                    Model model){

        if (!errors.hasErrors()) {
            Event event = eventTag.getEvent();
            Tag tag = eventTag.getTag();
            if (!event.getTags().contains(tag)){
                event.addTag(tag);
                eventRepository.save(event);
            }
            return "redirect:detail?eventId=" + event.getId();
        }

        return "redirect:add-tag";
    }

}
