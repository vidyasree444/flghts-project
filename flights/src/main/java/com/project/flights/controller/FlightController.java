package com.project.flights.controller;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.project.flights.dto.FlightResponseDto;
import com.project.flights.entity.City;
import com.project.flights.entity.Flight;
import com.project.flights.repository.CityRepo;
import com.project.flights.repository.FlightRepo;

@RestController
public class FlightController {
    @Autowired
    FlightRepo flightRepo;
    @Autowired
    CityRepo cityRepo;

    @GetMapping("/all")
public ResponseEntity<?> allflights(){
    try{
        List<Flight> allFlights= flightRepo.findAll();
        if(allFlights.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No data found");
        }
        List<FlightResponseDto> flightResponseDtos=new ArrayList<>();

        for(Flight f:allFlights){
            FlightResponseDto fed=new FlightResponseDto();
            fed.setAircraft(f.getAircraft());
            fed.setArrivalTime(f.getArrivalTime());
            fed.setDepartureTime(f.getDepartureTime());
            fed.setFlightNo(f.getFlightNo());
            fed.setFromCity(f.getFromCity().getCityCode());
            fed.setToCity(f.getToCity().getCityCode());
            fed.setDurationMinutes(f.getDurationMinutes());
            flightResponseDtos.add(fed);
            
        }

        return ResponseEntity.ok(flightResponseDtos);
    }catch(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occured while processing");
    }
}  
//6

    @PreAuthorize("hasRole('Admin')")
     @PostMapping("/add")
     public ResponseEntity<?> addFlight( @RequestBody Flight flight1,@RequestParam("fromCity") String fromCity,@RequestParam("toCity") String toCity){

          try{
        City fromCitycode = cityRepo.findById(fromCity).orElseThrow(() -> new IllegalArgumentException("From City not found"));
        City toCityCode= cityRepo.findById(toCity).orElseThrow(() -> new IllegalArgumentException("To City not found"));
        Flight flight =new Flight();
        
        flight.setFromCity(fromCitycode);
        flight.setToCity(toCityCode);
        
        flight.setFlightNo(flight1.getFlightNo());
        flight.setAircraft(flight1.getAircraft());
        flight.setDurationMinutes(flight1.getDurationMinutes());
       
        int dep_hour = flight1.getDepartureTime().getHour();
        int dep_min=flight1.getDepartureTime().getMinute();
        int dep_sec=flight1.getDepartureTime().getSecond();
        int dep_nano=flight1.getDepartureTime().getNano();

        LocalTime departure=LocalTime.of(dep_hour,dep_min,dep_sec,dep_nano);
        flight.setDepartureTime(departure);

        int arr_hour = flight1.getArrivalTime().getHour();
        int arr_min=flight1.getArrivalTime().getMinute();
        int arr_sec=flight1.getArrivalTime().getSecond();
        int arr_nano=flight1.getArrivalTime().getNano();

        LocalTime arrival=LocalTime.of(arr_hour,arr_min,arr_sec,arr_nano);
        flight.setArrivalTime(arrival);


      flightRepo.save(flight);
      return ResponseEntity.ok(flight);
    }catch( Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occured while adding new flight");
    }
}


    // @PostMapping("/adding")
    // public void adding(@RequestBody Flight flight){
    //     flightRepo.save(flight);
   

    // }

    // @GetMapping("listflights/{fromCity}/{toCity}")
    // public List<String> listing(@RequestParam ("fromCity") int fromCity,@RequestParam ("toCity") int toCity){
    //    return flightRepo.allCities(fromCity,toCity);

    // }
//3

    @GetMapping("/listflights/{fromCity}/{toCity}")
    public ResponseEntity<?> listFlight(@PathVariable ("fromCity") String fromCity ,@PathVariable("toCity") String toCity){

        try{
            List<Flight> allFlights= flightRepo.findByFromCityCityCodeAndToCityCityCode(fromCity,toCity);  
            if(allFlights.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No flights are avaliable between given cities");
            }
        
        
        List<FlightResponseDto> flightResponseDtos=new ArrayList<>();

        for(Flight f:allFlights){
            FlightResponseDto fed=new FlightResponseDto();
            fed.setAircraft(f.getAircraft());
            fed.setArrivalTime(f.getArrivalTime());
            fed.setDepartureTime(f.getDepartureTime());
            fed.setFlightNo(f.getFlightNo());
            fed.setFromCity(f.getFromCity().getCityCode());
            fed.setToCity(f.getToCity().getCityCode());
            fed.setDurationMinutes(f.getDurationMinutes());
            flightResponseDtos.add(fed);
            
        }return ResponseEntity.ok(flightResponseDtos);
    }
    catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occured while processing request");
    }
    }


//2
    @GetMapping("/flightpage/{num}/{size}")
    public ResponseEntity<?> allbypages(@RequestParam ("num")int num,@RequestParam ("size") int size){
        try {

        
            List<FlightResponseDto> flightResponseDtos =new ArrayList<>();

            List<Flight> flights =flightRepo.findAll(PageRequest.of(num,size)).getContent();

            for(Flight f: flights){
                FlightResponseDto frd=new FlightResponseDto();
                frd.setAircraft(f.getAircraft());
                frd.setArrivalTime(f.getArrivalTime());
                frd.setDepartureTime(f.getDepartureTime());
                frd.setFlightNo(f.getFlightNo());
                frd.setFromCity(f.getFromCity().getCityCode());
                frd.setToCity(f.getToCity().getCityCode());
                flightResponseDtos.add(frd);
            }

            return ResponseEntity.ok(flightResponseDtos);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid page number or Size");
        }
    }
}
