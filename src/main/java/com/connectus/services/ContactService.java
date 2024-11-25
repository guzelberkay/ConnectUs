package com.connectus.services;

import com.connectus.dto.request.*;
import com.connectus.entity.*;
import com.connectus.exception.GeneralException;
import com.connectus.exception.ErrorType;
import com.connectus.repository.*;
import com.connectus.utility.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;
    private final AuthRepository authRepository;
    private final JwtTokenManager jwtTokenManager;
    private final AddressRepository addressRepository;
    private final PhoneRepository phoneRepository;

    public Boolean save(ContactSaveRequestDTO dto) {

        Long authId = extractAuthIdFromToken(dto.token());
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        Contact contact = Contact.builder()
                .build();

        List<Address> addresses = dto.addresses().stream()
                .map(addressDTO -> Address.builder()
                        .description(addressDTO.description())
                        .value(addressDTO.value())
                        .contact(contact)
                        .build())
                .collect(Collectors.toList());

        List<Phone> phones = dto.phones().stream()
                .map(phoneDTO -> Phone.builder()
                        .description(phoneDTO.description())
                        .value(phoneDTO.value())
                        .contact(contact)
                        .build())
                .collect(Collectors.toList());

        contact.setAddresses(addresses);
        contact.setPhones(phones);

        addressRepository.saveAll(addresses);
        phoneRepository.saveAll(phones);

        contactRepository.save(contact);

        return true;
    }

    public Boolean delete(DeleteRequestDTO dto) {
        Long authId = extractAuthIdFromToken(dto.token());
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));


        Contact contact = contactRepository.findById(dto.contactId())
                .orElseThrow(() -> new GeneralException(ErrorType.CONTACT_NOT_FOUND));

        Boolean deleted = false;

        if (dto.addressIds() != null && !dto.addressIds().isEmpty()) {
            List<Address> addressesToDelete = addressRepository.findAllById(dto.addressIds());
            addressRepository.deleteAll(addressesToDelete);
            deleted = true;
        }

        if (dto.phoneIds() != null && !dto.phoneIds().isEmpty()) {
            List<Phone> phonesToDelete = phoneRepository.findAllById(dto.phoneIds());
            phoneRepository.deleteAll(phonesToDelete);
            deleted = true;
        }

        return deleted;
    }


    public Boolean update( ContactUpdateDTO dto) {

        Long authId = extractAuthIdFromToken(dto.token());
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        Contact contact = contactRepository.findById(dto.contactId())
                .orElseThrow(() -> new GeneralException(ErrorType.CONTACT_NOT_FOUND));


        if (dto.addresses() != null && !dto.addresses().isEmpty()) {

            contact.getAddresses().clear();
            List<Address> addresses = dto.addresses().stream()
                    .map(addressDTO -> Address.builder()
                            .description(addressDTO.description())
                            .value(addressDTO.value())
                            .contact(contact)
                            .build())
                    .collect(Collectors.toList());
            contact.getAddresses().addAll(addresses);
        }


        if (dto.phones() != null && !dto.phones().isEmpty()) {

            contact.getPhones().clear();
            List<Phone> phones = dto.phones().stream()
                    .map(phoneDTO -> Phone.builder()
                            .description(phoneDTO.description())
                            .value(phoneDTO.value())
                            .contact(contact)
                            .build())
                    .collect(Collectors.toList());
            contact.getPhones().addAll(phones);
        }

        contactRepository.save(contact);

        return true;
    }

    public List<Contact> findAll() {
        return contactRepository.findAll();
    }

    private Long extractAuthIdFromToken(String token) {
        Optional<Long> authIdOptional = jwtTokenManager.getAuthIdFromToken(token);
        if (authIdOptional.isPresent()) {
            return authIdOptional.get();
        } else {
            throw new RuntimeException("AuthId could not be extracted from token");
        }
    }
}
