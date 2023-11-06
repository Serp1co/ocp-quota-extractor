package com.redhat.quota.extractor.services.interfaces;

import com.redhat.quota.extractor.exception.ApplicationException;

import java.util.Optional;

public interface ILoginService {
    Optional<String> login() throws ApplicationException;
}
