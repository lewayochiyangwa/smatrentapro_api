alter procedure spLeePortfolio_summary(

    @ClientID bigint,
    @date SMALLDATETIME
    )
    as

begin

declare @MyTemp_Summary table
(
  InvestmentType INT,
  ItemType INT,
  InvestmentTypeName VARCHAR(255),
  InvestmentIndustry VARCHAR(255),
  CounterpartyID INT,
  LongName VARCHAR(255),
  ClientName VARCHAR(255),
  AccountID INT,
  AccountNo VARCHAR(255),
  InvestmentID INT,
  InvestmentName VARCHAR(255),
  ValueDate DATE,
  Quantity DECIMAL(18, 5),
  Cost DECIMAL(18, 5),
  WACTenor DECIMAL(18, 5),
  Price DECIMAL(18, 5),
  MaturityDate DATE,
  ReturnValue DECIMAL(18, 5),
  Gain DECIMAL(18, 5),
  SectorExposure DECIMAL(18, 5),
  OverallExposure DECIMAL(18, 5),
  CostSectorExposure DECIMAL(18, 5),
  CostOverallExposure DECIMAL(18, 5),
  Percentage DECIMAL(18, 5),
  TotalValue DECIMAL(18, 5),
  QtyLabel VARCHAR(255),
  InvestmentLabel VARCHAR(255),
  CostLabel VARCHAR(255),
  WACTenorLabel VARCHAR(255),
  PriceLabel VARCHAR(255),
  GainLabel VARCHAR(255),
  ReturnValueLabel VARCHAR(255),
  Date DATE,
  IsClient INT,
  PensionFund INT,
  ClientType VARCHAR(255)
);

EXEC spConsolidatedPortfolioWebSet @ClientID, @date



Insert  @MyTemp_Summary EXEC spConsolidatedPortfolioWebGet @ClientID, @date

SELECT v.InvestmentIndustry as Name,coalesce(sum(v.Cost),0.0) as BookValue,coalesce(sum(v.price),0.0) as MarketValue,coalesce(sum(v.SectorExposure),0.0) as percBookValue,coalesce(sum(OverallExposure),0.0) as percMarketValue FROM @MyTemp_Summary v where v.InvestmentIndustry=v.InvestmentIndustry group by InvestmentIndustry

end

--spLeePortfolio_summary  '873','1 jan 2024'