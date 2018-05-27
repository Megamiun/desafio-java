package br.com.gabryel.logineer.service.impl;

import br.com.gabryel.logineer.service.TimeProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TimeProviderImpl implements TimeProvider {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
