package br.com.josiasmartins.picpaydesafiobackend.transaction;

import br.com.josiasmartins.picpaydesafiobackend.authorization.AuthorizerService;
import br.com.josiasmartins.picpaydesafiobackend.notification.NotificationService;
import br.com.josiasmartins.picpaydesafiobackend.wallet.Wallet;
import br.com.josiasmartins.picpaydesafiobackend.wallet.WalletRepository;
import br.com.josiasmartins.picpaydesafiobackend.wallet.WalletType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    private Wallet wallet;

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final AuthorizerService authorizerService;
    private NotificationService notificationService;

    public TransactionService(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            AuthorizerService authorizerService,
            NotificationService notificationService) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.authorizerService = authorizerService;
        this.notificationService = notificationService;
    }

    @Transactional // se uma operacao falhar, será feito um rollback que forem feitos no banco
    public Transaction create(Transaction transaction) {
        // 1 - validar
        validate(transaction);

        // 2 - criar a transação
        Transaction newTransaction = transactionRepository.findById(transaction.payer()).get();
        walletRepository.save(wallet.debit(transaction.value()));

        // 3 - debitar da carteira
        var walletPayer = walletRepository.findById(transaction.payer()).get();
        var walletPayee = walletRepository.findById(transaction.payer()).get();
        walletRepository.save(walletPayer.debit(transaction.value()));
        walletRepository.save(walletPayee.credit(transaction.value()));

        // 4 - chamar serviços externos
        // authorization transaction
        authorizerService.authorize(transaction);

        // notificacao
        notificationService.notify(transaction);

        return newTransaction;
    }

    /*
    * - the payer has a common wallet
    * - the payer has enough balance
    * - the payer is not the balance
    */
    public void validate(Transaction transaction) {
        walletRepository.findById(transaction.payee())
                .map(payee -> walletRepository.findById(transaction.payer())
                        .map(payer -> isTransactionValid(transaction, payer) ? transaction : null)
                        .orElseThrow(() -> new InvalidTransactionException("Invalid transaction - %s".formatted(transaction))))
                .orElseThrow(() -> new InvalidTransactionException("Invalid transaction - %s".formatted(transaction)));
    }

    private boolean isTransactionValid(Transaction transaction, Wallet payer) {
        return payer.type() == WalletType.COMUM.getValue() &&
                payer.balance().compareTo(transaction.value()) >= 0 &&
                !payer.id().equals(transaction.payer());
    }

    public List<Transaction> list() {
        return this.transactionRepository.findAll();
    }


}
