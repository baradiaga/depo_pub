package com.moscepa.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration()
                // Stratégie STRICTE pour éviter les mappings ambigus
                .setMatchingStrategy(MatchingStrategies.STRICT)

                // --- LA CORRECTION EST ICI ---
                // Active la correspondance des constructeurs. ModelMapper va maintenant essayer
                // de faire correspondre les propriétés de la source aux paramètres du constructeur
                // de la destination.
                // C'est ce qui lui permet de construire des 'record'.
                .setDestinationNamingConvention(
                        org.modelmapper.convention.NamingConventions.JAVABEANS_MUTATOR)
                .setSourceNamingConvention(
                        org.modelmapper.convention.NamingConventions.JAVABEANS_ACCESSOR);

        return mapper;
    }
}
