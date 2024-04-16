package com.example.lakesidehotel.service;

import com.example.lakesidehotel.exception.InvalidBookingRequestException;
import com.example.lakesidehotel.exception.ResourceNotFoundException;
import com.example.lakesidehotel.model.BookedRoom;
import com.example.lakesidehotel.model.Room;
import com.example.lakesidehotel.repository.BookRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookedRoomService {

    private final BookRoomRepository bookRoomRepository;
    private final RoomService roomService;

    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookRoomRepository.findByRoomId(roomId);
    }

    public void cancelBooking(Long bookingId) {
        bookRoomRepository.deleteById(bookingId);
    }

    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
            throw new InvalidBookingRequestException("Check-in date must come before check-out date");
        }
        Room room = roomService.getRoomById(roomId).get();
        List<BookedRoom> bookings = room.getBookings();
        boolean roomIsAvailable = roomIsAvailable(bookingRequest, bookings);
        if (roomIsAvailable){
            room.addBooking(bookingRequest);
            bookRoomRepository.save(bookingRequest);
        }else{
            throw new InvalidBookingRequestException("This room is not available  for the selected dates");
        }
        return bookingRequest.getBookingConfirmationCode();
    }



    public BookedRoom findByBookingConfirmationCode(String confirmationCode) {
        return bookRoomRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(() -> {
            throw new ResourceNotFoundException("No booking found with booking code : " + confirmationCode);
        });

    }

    public List<BookedRoom> getAllBookings() {
        return bookRoomRepository.findAll();
    }

    private boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }

    public List<BookedRoom> getBookingsByUserEmail(String email) {
        return bookRoomRepository.findByGuestEmail(email);
    }
}
