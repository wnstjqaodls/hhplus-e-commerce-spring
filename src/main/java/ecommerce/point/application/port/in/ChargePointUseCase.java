package ecommerce.point.application.port.in;

public interface ChargePointUseCase {
    long charge (Long pointId, long amount);
}
