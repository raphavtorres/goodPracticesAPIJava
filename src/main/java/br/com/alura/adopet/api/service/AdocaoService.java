package br.com.alura.adopet.api.service;

import br.com.alura.adopet.api.dto.AprovacaoAdocaoDto;
import br.com.alura.adopet.api.dto.ReprovacaoDto;
import br.com.alura.adopet.api.dto.SolicitacaoAdocaoDto;
import br.com.alura.adopet.api.exception.ValidacaoException;
import br.com.alura.adopet.api.model.Adocao;
import br.com.alura.adopet.api.model.Pet;
import br.com.alura.adopet.api.model.StatusAdocao;
import br.com.alura.adopet.api.model.Tutor;
import br.com.alura.adopet.api.repository.AdocaoRepository;
import br.com.alura.adopet.api.repository.PetRepository;
import br.com.alura.adopet.api.repository.TutorRepository;
import br.com.alura.adopet.api.validacoes.ValidacaoSolicitacaoAdocao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AdocaoService {

    @Autowired
    private AdocaoRepository adocaoRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private List<ValidacaoSolicitacaoAdocao> validacoes;

    public void solicitar(SolicitacaoAdocaoDto solicitacaoAdocaoDto) {

        Tutor tutor = tutorRepository.getReferenceById(solicitacaoAdocaoDto.idTutor());
        Pet pet = petRepository.getReferenceById(solicitacaoAdocaoDto.idPet());

        validacoes.forEach(validador -> validador.validar(solicitacaoAdocaoDto));

        Adocao adocao = new Adocao(tutor, pet,  solicitacaoAdocaoDto.motivo());
        adocaoRepository.save(adocao);

        var to = adocao.getPet().getAbrigo().getEmail();
        var subject = "Solicitação de adoção";
        var message = "Olá " + adocao.getPet().getAbrigo().getNome() + "!\n\nUma solicitação de adoção foi registrada hoje para o pet: " + adocao.getPet().getNome() + ". \nFavor avaliar para aprovação ou reprovação.";
        emailSenderService.enviarEmail(to, subject, message);
    }

    public void aprovar(AprovacaoAdocaoDto aprovacaoAdocaoDto) {

        Adocao adocao = adocaoRepository.getReferenceById(aprovacaoAdocaoDto.idAdocao());
        adocao.marcarComoAprovado();

        var to = adocao.getTutor().getEmail();
        var subject = "Adoção aprovada";
        var message = "Parabéns " + adocao.getTutor().getNome() + "!\n\nSua adoção do pet " + adocao.getPet().getNome() + ", solicitada em " + adocao.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + ", foi aprovada.\nFavor entrar em contato com o abrigo " + adocao.getPet().getAbrigo().getNome() + " para agendar a busca do seu pet.";

        emailSenderService.enviarEmail(to, subject, message);
    }

    public void reprovar(ReprovacaoDto reprovacaoDto) {

        Adocao adocao = adocaoRepository.getReferenceById(reprovacaoDto.idAdocao());
        adocao.marcarComoReprovada(reprovacaoDto.justificativa());

        var to = adocao.getTutor().getEmail();
        var subject = "Adoção reprovada";
        var message = "Olá " + adocao.getTutor().getNome() + "!\n\nInfelizmente sua adoção do pet " + adocao.getPet().getNome() + ", solicitada em " + adocao.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + ", foi reprovada pelo abrigo " + adocao.getPet().getAbrigo().getNome() + " com a seguinte justificativa: " + adocao.getJustificativaStatus();

        emailSenderService.enviarEmail(to, subject, message);

    }
}
