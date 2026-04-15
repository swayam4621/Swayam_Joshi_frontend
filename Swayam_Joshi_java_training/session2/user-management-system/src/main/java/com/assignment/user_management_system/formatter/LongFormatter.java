package com.assignment.user_management_system.formatter;
import org.springframework.stereotype.Component;

@Component("LONG")
public class LongFormatter implements MessageFormatter {
    public String format(String name) { return "Welcome to the system, " + name + ". Your account is now active."; }
}